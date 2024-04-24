package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import spock.lang.Specification
import spock.lang.Subject

import static CancellingRequest.Command
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsFixture.clientWithNoRequests
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsFixture.clientWithRequest

class CancellingRequestTest extends Specification {
  
  ClientRequestsRepository clientRequestsRepository = Mock()
  
  @Subject
  CancellingRequest cancellingRequest = new CancellingRequest(clientRequestsRepository)
  
  def "allow to cancel client request"() {
    given:
      def requestId = RequestId.newOne()
    and:
      clientRequestsRepository.findBy(requestId) >> Option.of(clientWithRequest(requestId))
    
    when:
      def result = cancellingRequest.cancelRequest(new Command(requestId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject cancelling client request when there is no such for client"() {
    given:
      def requestId = RequestId.newOne()
    and:
      clientRequestsRepository.findBy(requestId) >> Option.of(clientWithNoRequests())
    
    when:
      def result = cancellingRequest.cancelRequest(new Command(requestId))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def "fail to cancel client request when cannot find client requests for such request"() {
    given:
      def requestId = RequestId.newOne()
    and:
      clientRequestsRepository.findBy(requestId) >> Option.none()
    
    when:
      def result = cancellingRequest.cancelRequest(new Command(requestId))
    
    then:
      result.isFailure()
  }
  
}
