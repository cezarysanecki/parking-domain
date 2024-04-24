package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model

import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import spock.lang.Specification

import static ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForAnyRequests
import static ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class CancellingParkingSpotRequestTest extends Specification {
  
  def "allow to cancel parking spot request"() {
    given:
      def requestId = RequestId.newOne()
    and:
      def parkingSpotRequests = parkingSpotWithoutPlaceForAnyRequests(requestId)
    
    when:
      def result = parkingSpotRequests.cancel(requestId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.requestId == requestId
      }
  }
  
  def "reject cancelling request for parking spot when this is no such request for that parking spot"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutRequests()
    and:
      def requestId = RequestId.newOne()
    
    when:
      def result = parkingSpotRequests.cancel(requestId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.requestId == requestId
        assert it.reason == "there is no such request on that parking spot"
      }
  }
  
}
