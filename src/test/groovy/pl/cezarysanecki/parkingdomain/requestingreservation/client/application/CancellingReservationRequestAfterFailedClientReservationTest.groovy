package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithReservationRequest
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.StoringParkingSpotReservationRequestFailed

class CancellingReservationRequestAfterFailedClientReservationTest extends Specification {
  
  ClientReservationRequestsRepository clientReservationRequestsRepository = Mock()
  
  @Subject
  CancellingReservationRequestEventHandler cancellingReservationRequestEventHandler = new CancellingReservationRequestEventHandler(
      new CancellingReservationRequest(clientReservationRequestsRepository))
  
  def "cancel client reservation request when requesting reservation on parking spot fails"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def clientReservationRequests = clientWithReservationRequest(reservationId)
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.of(clientReservationRequests)
    
    when:
      cancellingReservationRequestEventHandler.handle(new StoringParkingSpotReservationRequestFailed(
          ParkingSpotId.newOne(), reservationId, "any reason"))
    
    then:
      1 * clientReservationRequestsRepository.publish({
        it.clientId == clientReservationRequests.clientId
            && it.reservationId == reservationId
      } as ReservationRequestCancelled)
  }
  
}
