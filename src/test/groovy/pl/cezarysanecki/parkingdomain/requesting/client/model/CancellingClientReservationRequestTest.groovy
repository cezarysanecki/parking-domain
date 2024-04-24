package pl.cezarysanecki.parkingdomain.requesting.client.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithNoRequests
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest

class CancellingClientReservationRequestTest extends Specification {
  
  def "allow to create client reservation request for part of parking spot"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def clientReservationRequests = clientWithRequest(reservationId)
    
    when:
      def result = clientReservationRequests.cancel(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientReservationRequests.clientId
        assert it.reservationId == reservationId
      }
  }
  
  def "reject creating client reservation request for part of parking spot when limit of request is reached"() {
    given:
      def clientReservationRequests = clientWithNoRequests()
    
    when:
      def result = clientReservationRequests.cancel(ReservationId.newOne())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientReservationRequests.clientId
        assert it.reservationId == reservationId
        assert it.reason == "there is no such reservation request"
      }
  }
  
}
