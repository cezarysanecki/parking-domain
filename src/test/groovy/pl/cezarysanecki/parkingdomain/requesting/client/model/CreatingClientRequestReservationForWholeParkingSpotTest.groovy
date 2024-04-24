package pl.cezarysanecki.parkingdomain.requesting.client.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithNoRequests

class CreatingClientRequestReservationForWholeParkingSpotTest extends Specification {
  
  def "allow to create client reservation request for whole parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def clientReservationRequests = clientWithNoRequests()
    
    when:
      def result = clientReservationRequests.createRequest(parkingSpotId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientReservationRequests.clientId
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "reject creating client reservation request for whole parking spot when limit of request is reached"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def clientReservationRequests = clientWithRequest(ReservationId.newOne())
    
    when:
      def result = clientReservationRequests.createRequest(parkingSpotId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientReservationRequests.clientId
        assert it.reason == "client has too many requests"
      }
  }
  
}
