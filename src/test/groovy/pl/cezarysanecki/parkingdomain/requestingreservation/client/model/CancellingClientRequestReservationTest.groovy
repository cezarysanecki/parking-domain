package pl.cezarysanecki.parkingdomain.requestingreservation.client.model


import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientReservationsWithReservation
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.noClientReservations

class CancellingClientRequestReservationTest extends Specification {
  
  def "allow to create client reservation request for part of parking spot"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def clientReservations = clientReservationsWithReservation(reservationId)
    
    when:
      def result = clientReservations.cancel(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientReservations.clientId
        assert it.reservationId == reservationId
      }
  }
  
  def "reject creating client reservation request for part of parking spot when limit of request is reached"() {
    given:
      def clientReservations = noClientReservations()
    
    when:
      def result = clientReservations.cancel(ReservationId.newOne())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientReservations.clientId
        assert it.reservationId == reservationId
        assert it.reason == "there is no such reservation"
      }
  }
  
}
