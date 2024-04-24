package pl.cezarysanecki.parkingdomain.requesting.client.model

import spock.lang.Specification

import static ClientRequestsFixture.clientWithNoRequests
import static ClientRequestsFixture.clientWithRequest

class CancellingClientRequestTest extends Specification {
  
  def "allow to make client request for part of parking spot"() {
    given:
      def requestId = RequestId.newOne()
    and:
      def clientRequests = clientWithRequest(requestId)
    
    when:
      def result = clientRequests.cancel(requestId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientRequests.clientId
        assert it.requestId == requestId
      }
  }
  
  def "reject making client request for part of parking spot when limit of request is reached"() {
    given:
      def clientRequests = clientWithNoRequests()
    
    when:
      def result = clientRequests.cancel(RequestId.newOne())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientRequests.clientId
        assert it.requestId == requestId
        assert it.reason == "there is no such request"
      }
  }
  
}
