package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed

class CancellingRequestAfterFailedClientTest extends Specification {
  
  ClientRequestsRepository clientRequestsRepository = Mock()
  
  @Subject
  CancellingRequestEventHandler cancellingRequestEventHandler = new CancellingRequestEventHandler(
      new CancellingRequest(clientRequestsRepository))
  
  def "cancel client request when making request on parking spot fails"() {
    given:
      def requestId = RequestId.newOne()
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
