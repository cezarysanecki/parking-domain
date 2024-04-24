package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed

class CancellingReservationRequestAfterFailedClientTest extends Specification {
  
  ClientRequestsRepository clientReservationRequestsRepository = Mock()
  
  @Subject
  CancellingRequestEventHandler cancellingReservationRequestEventHandler = new CancellingRequestEventHandler(
      new CancellingRequest(clientReservationRequestsRepository))
  
  def "cancel client reservation request when requesting reservation on parking spot fails"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def clientReservationRequests = clientWithRequest(reservationId)
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.of(clientReservationRequests)
    
    when:
      cancellingReservationRequestEventHandler.handle(new StoringParkingSpotRequestFailed(
          ParkingSpotId.newOne(), reservationId, "any reason"))
    
    then:
      1 * clientReservationRequestsRepository.publish({
        it.clientId == clientReservationRequests.clientId
            && it.reservationId == reservationId
      } as RequestCancelled)
  }
  
}
