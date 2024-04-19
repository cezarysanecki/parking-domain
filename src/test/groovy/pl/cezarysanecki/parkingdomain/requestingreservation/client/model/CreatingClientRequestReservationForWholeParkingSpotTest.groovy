package pl.cezarysanecki.parkingdomain.requestingreservation.client.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithReservationRequest
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithNoReservationRequests

class CreatingClientRequestReservationForWholeParkingSpotTest extends Specification {
  
  def "allow to create client reservation request for whole parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def clientReservationRequests = clientWithNoReservationRequests()
    
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
      def clientReservationRequests = clientWithReservationRequest(ReservationId.newOne())
    
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
