package pl.cezarysanecki.parkingdomain.requestingreservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ClientRequestsEvent.RequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ClientRequestsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed

class CancellingReservationRequestAfterFailedClientTest extends Specification {
  
  ReservationRequesterRepository clientRequestsRepository = Mock()
  
  @Subject
  CancellingRequestEventHandler cancellingRequestEventHandler = new CancellingRequestEventHandler(
      new CancellingReservationRequest(clientRequestsRepository))
  
  def "cancel client request when making request on parking spot fails"() {
    given:
      def requestId = ReservationRequestId.newOne()
    and:
      def clientRequests = clientWithRequest(requestId)
    and:
      clientRequestsRepository.findBy(requestId) >> Option.of(clientRequests)
    
    when:
      cancellingRequestEventHandler.handle(new StoringParkingSpotRequestFailed(
          ParkingSpotId.newOne(), requestId, "any reason"))
    
    then:
      1 * clientRequestsRepository.publish({
        it.clientId == clientRequests.clientId
            && it.requestId == requestId
      } as RequestCancelled)
  }
  
}
