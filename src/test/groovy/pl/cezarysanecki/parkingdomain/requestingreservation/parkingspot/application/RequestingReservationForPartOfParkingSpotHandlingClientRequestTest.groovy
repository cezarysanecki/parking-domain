package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.StoringParkingSpotReservationRequestFailed
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.fullyReservedParkingSpotBy
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservations

class RequestingReservationForPartOfParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationId reservationId = ReservationId.newOne()
  
  ParkingSpotReservationRequestsRepository parkingSpotReservationsRepository = Mock()
  
  @Subject
  StoringParkingSpotReservationRequestEventHandler clientReservationsEventHandler = new StoringParkingSpotReservationRequestEventHandler(parkingSpotReservationsRepository)
  
  def "reserve part of parking spot when client made a request for it"() {
    given:
      def vehicleSize = VehicleSize.of(2)
    and:
      def parkingSpotReservations = noParkingSpotReservations()
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotReservations.parkingSpotId) >> Option.of(parkingSpotReservations)
    
    when:
      clientReservationsEventHandler.handle(new ReservationForPartOfParkingSpotRequested(
          clientId, reservationId, parkingSpotReservations.parkingSpotId, vehicleSize))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotReservations.parkingSpotId
            && it.reservationId == reservationId
            && it.vehicleSize == vehicleSize
      } as ReservationRequestForPartOfParkingSpotStored)
  }
  
  def "reject reserving part of parking spot when client made a request for it when there is not enough place"() {
    given:
      def parkingSpotReservations = fullyReservedParkingSpotBy(ReservationId.newOne())
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotReservations.parkingSpotId) >> Option.of(parkingSpotReservations)
    
    when:
      clientReservationsEventHandler.handle(new ReservationForPartOfParkingSpotRequested(
          clientId, reservationId, parkingSpotReservations.parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotReservations.parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotReservationRequestFailed)
  }
  
  def "reject reserving part of parking spot when client made a request for it when parking spot reservations does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotReservationsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      clientReservationsEventHandler.handle(new ReservationForPartOfParkingSpotRequested(
          clientId, reservationId, parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotReservationsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotReservationRequestFailed)
  }
  
}
