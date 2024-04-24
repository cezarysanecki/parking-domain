package pl.cezarysanecki.parkingdomain.requesting.client.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import spock.lang.Specification

import static ClientRequestsFixture.clientWithNoRequests
import static ClientRequestsFixture.clientWithRequest

class CreatingClientRequestForWholeParkingSpotTest extends Specification {
  
  def "allow to make client request for whole parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def clientRequests = clientWithNoRequests()
    
    when:
      def result = clientRequests.createRequest(parkingSpotId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientRequests.clientId
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "reject making client request for whole parking spot when limit of request is reached"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def clientRequests = clientWithRequest(RequestId.newOne())
    
    when:
      def result = clientRequests.createRequest(parkingSpotId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientRequests.clientId
        assert it.reason == "client has too many requests"
      }
  }
  
}
