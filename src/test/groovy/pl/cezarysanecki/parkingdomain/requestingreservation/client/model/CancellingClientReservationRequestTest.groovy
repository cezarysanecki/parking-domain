package pl.cezarysanecki.parkingdomain.requestingreservation.client.model

import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithNoReservationRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithReservationRequest

class CancellingClientReservationRequestTest extends Specification {
  
  def "allow to create client reservation request for part of parking spot"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def clientReservationRequests = clientWithReservationRequest(reservationId)
    
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
      def clientReservationRequests = clientWithNoReservationRequests()
    
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
