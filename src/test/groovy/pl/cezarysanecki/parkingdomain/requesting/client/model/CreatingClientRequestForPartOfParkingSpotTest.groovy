package pl.cezarysanecki.parkingdomain.requesting.client.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import spock.lang.Specification

import static ClientRequestsFixture.clientWithNoRequests
import static ClientRequestsFixture.clientWithRequest

class CreatingClientRequestForPartOfParkingSpotTest extends Specification {
  
  def "allow to make client request for part of parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def vehicleSize = VehicleSize.of(2)
    and:
      def clientRequests = clientWithNoRequests()
    
    when:
      def result = clientRequests.createRequest(parkingSpotId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientRequests.clientId
        assert it.parkingSpotId == parkingSpotId
        assert it.vehicleSize == vehicleSize
      }
  }
  
  def "reject making client request for part of parking spot when limit of request is reached"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def vehicleSize = VehicleSize.of(2)
    and:
      def clientRequests = clientWithRequest(RequestId.newOne())
    
    when:
      def result = clientRequests.createRequest(parkingSpotId, vehicleSize)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientRequests.clientId
        assert it.reason == "client has too many requests"
      }
  }
  
}
