package pl.cezarysanecki.parkingdomain.requestingreservation.client.model

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import spock.lang.Specification

import static ClientRequestsFixture.clientWithNoRequests
import static ClientRequestsFixture.clientWithRequest

class CreatingClientRequestForPartOfParkingSpotTest extends Specification {
  
  def "allow to make client request for part of parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def vehicleSize = SpotUnits.of(2)
    and:
      def clientRequests = clientWithNoRequests()
    
    when:
      def result = clientRequests.createRequest(parkingSpotId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientRequests.clientId
        assert it.parkingSpotId == parkingSpotId
        assert it.spotUnits == vehicleSize
      }
  }
  
  def "reject making client request for part of parking spot when limit of request is reached"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def vehicleSize = SpotUnits.of(2)
    and:
      def clientRequests = clientWithRequest(ReservationRequestId.newOne())
    
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
