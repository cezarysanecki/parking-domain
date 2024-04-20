package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForWholeParkingSpotRequested
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForWholeParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.StoringParkingSpotReservationRequestFailed
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutPlaceForReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutReservationRequests

class RequestingReservationOnWholeParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationId reservationId = ReservationId.newOne()
  
  ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  StoringParkingSpotReservationRequestEventHandler storingParkingSpotReservationRequestEventHandler = new StoringParkingSpotReservationRequestEventHandler(parkingSpotReservationRequestsRepository)
  
  def "request reservation on whole parking spot when client made a request for it"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new ReservationForWholeParkingSpotRequested(
          clientId, reservationId, parkingSpotReservationRequests.parkingSpotId))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
            && it.reservationId == reservationId
      } as ReservationRequestForWholeParkingSpotStored)
  }
  
  def "reject requesting reservation on whole parking spot when client made a request for it when there is reservation already"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutPlaceForReservationRequests(ReservationId.newOne())
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotReservationRequests.parkingSpotId) >> Option.of(parkingSpotReservationRequests)
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new ReservationForWholeParkingSpotRequested(
          clientId, reservationId, parkingSpotReservationRequests.parkingSpotId))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotReservationRequestFailed)
  }
  
  def "reject requesting reservation on whole parking spot when client made a request for it when parking spot reservations does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotReservationRequestsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      storingParkingSpotReservationRequestEventHandler.handle(new ReservationForWholeParkingSpotRequested(
          clientId, reservationId, parkingSpotId))
    
    then:
      1 * parkingSpotReservationRequestsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.reservationId == reservationId
      } as StoringParkingSpotReservationRequestFailed)
  }
  
}
