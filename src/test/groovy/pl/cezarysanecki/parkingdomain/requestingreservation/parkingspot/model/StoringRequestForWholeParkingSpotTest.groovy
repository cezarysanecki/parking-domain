package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model

import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import spock.lang.Specification

import static ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForAnyRequests
import static ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class StoringRequestForWholeParkingSpotTest extends Specification {
  
  def "allow to store request for whole parking spot"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutRequests()
    and:
      def requestId = ReservationRequestId.newOne()
    
    when:
      def result = parkingSpotRequests.storeRequest(requestId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.reservationRequestId == requestId
      }
  }
  
  def "reject storing request for whole parking spot when there is at least one request"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutPlaceForAnyRequests(ReservationRequestId.newOne())
    and:
      def requestId = ReservationRequestId.newOne()
    
    when:
      def result = parkingSpotRequests.storeRequest(requestId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.reservationRequestId == requestId
        assert it.reason == "there are requests for this parking spot"
      }
  }
  
}
