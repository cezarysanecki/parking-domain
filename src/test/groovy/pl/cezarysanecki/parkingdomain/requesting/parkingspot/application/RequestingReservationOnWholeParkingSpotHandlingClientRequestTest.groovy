package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutPlaceForReservationRequests
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutReservationRequests

class RequestingReservationOnWholeParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationId reservationId = ReservationId.newOne()
  
  ParkingSpotRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  StoringParkingSpotRequestEventHandler storingParkingSpotReservationRequestEventHandler = new StoringParkingSpotRequestEventHandler(parkingSpotReservationRequestsRepository)
  
  def "request reservation on whole parking spot when client made a request for it"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new RequestForWholeParkingSpotMade(
          clientId, reservationId, parkingSpotReservationRequests.parkingSpotId))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
            && it.reservationId == reservationId
      } as RequestForWholeParkingSpotStored)
  }
  
  def "reject requesting reservation on whole parking spot when client made a request for it when there is reservation already"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutPlaceForReservationRequests(ReservationId.newOne())
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new RequestForWholeParkingSpotMade(
          clientId, reservationId, parkingSpotReservationRequests.parkingSpotId))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotRequestFailed)
  }
  
  def "reject requesting reservation on whole parking spot when client made a request for it when parking spot reservations does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new RequestForWholeParkingSpotMade(
          clientId, reservationId, parkingSpotId))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotRequestFailed)
  }
  
}
