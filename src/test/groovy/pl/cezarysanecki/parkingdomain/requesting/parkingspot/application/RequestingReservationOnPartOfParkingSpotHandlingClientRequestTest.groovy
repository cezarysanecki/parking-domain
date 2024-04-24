package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutPlaceForReservationRequests
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutReservationRequests

class RequestingReservationOnPartOfParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationId reservationId = ReservationId.newOne()
  
  ParkingSpotRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  StoringParkingSpotRequestEventHandler storingParkingSpotReservationRequestEventHandler = new StoringParkingSpotRequestEventHandler(parkingSpotReservationRequestsRepository)
  
  def "request reservation on part of parking spot when client made a request for it"() {
    given:
      def vehicleSize = VehicleSize.of(2)
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, reservationId, parkingSpotReservationRequests.parkingSpotId, vehicleSize))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
            && it.reservationId == reservationId
            && it.vehicleSize == vehicleSize
      } as RequestForPartOfParkingSpotStored)
  }
  
  def "reject requesting reservation on part of parking spot when client made a request for it when there is not enough place"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutPlaceForReservationRequests(ReservationId.newOne())
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, reservationId, parkingSpotReservationRequests.parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotRequestFailed)
  }
  
  def "reject requesting reservation on part of parking spot when client made a request for it when parking spot reservations does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, reservationId, parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotRequestFailed)
  }
  
}
