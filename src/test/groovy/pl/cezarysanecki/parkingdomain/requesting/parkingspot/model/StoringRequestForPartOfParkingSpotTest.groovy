package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model

import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import spock.lang.Specification

import static ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForRequestsWithCapacity
import static ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class StoringRequestForPartOfParkingSpotTest extends Specification {
  
  def "allow to store request for part of parking spot"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutRequests()
    and:
      def requestId = RequestId.newOne()
    and:
      def vehicleSize = VehicleSize.of(2)
    
    when:
      def result = parkingSpotRequests.storeRequest(requestId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.requestId == requestId
        assert it.vehicleSize == vehicleSize
      }
  }
  
  def "reject storing request for part of parking spot when there is not enough space"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutPlaceForRequestsWithCapacity(2)
    and:
      def requestId = RequestId.newOne()
    
    when:
      def result = parkingSpotRequests.storeRequest(requestId, VehicleSize.of(3))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotRequests.parkingSpotId
        assert it.requestId == requestId
        assert it.reason == "not enough parking spot space"
      }
  }
  
}
