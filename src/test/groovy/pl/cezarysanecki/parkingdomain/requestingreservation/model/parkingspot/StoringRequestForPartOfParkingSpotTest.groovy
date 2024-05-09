package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot


import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits
import spock.lang.Specification

import static ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForRequestsWithCapacity
import static ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class StoringRequestForPartOfParkingSpotTest extends Specification {
  
  def "allow to store request for part of parking spot"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutRequests()
    and:
      def requestId = ReservationRequestId.newOne()
    and:
      def vehicleSize = SpotUnits.of(2)
    
    when:
      def result = parkingSpotRequests.storeRequest(requestId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.reservationRequestId == requestId
        assert it.spotUnits == vehicleSize
      }
  }
  
  def "reject storing request for part of parking spot when there is not enough space"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutPlaceForRequestsWithCapacity(2)
    and:
      def requestId = ReservationRequestId.newOne()
    
    when:
      def result = parkingSpotRequests.storeRequest(requestId, SpotUnits.of(3))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.reservationRequestId == requestId
        assert it.reason == "not enough parking spot space"
      }
  }
  
}
