package pl.cezarysanecki.parkingdomain.client.reservationrequest.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import spock.lang.Specification

import java.time.LocalDateTime

import static ClientReservationRequestsFixture.anyClientId
import static ClientReservationRequestsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.CancellationOfReservationRequestFailed
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.noReservationRequests

class CancellingReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  ReservationId reservationId = anyReservationId()
  
  LocalDateTime now = LocalDateTime.now()
  
  def "can cancel reservation for parking spot"() {
    given:
      def clientReservationRequests = reservationRequestsWith(clientId, reservationId, now)
    
    when:
      Either<CancellationOfReservationRequestFailed, ReservationRequestCancelled> result = clientReservationRequests.cancel(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationId == reservationId
      }
  }
  
  def "cannot cancel reservation for parking spot when there is no such reservation"() {
    given:
      def clientReservationRequests = noReservationRequests(clientId, now)
    
    when:
      Either<CancellationOfReservationRequestFailed, ReservationRequestCancelled> result = clientReservationRequests.cancel(reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reservationId == reservationId
        assert it.reason == "does not have this reservation"
      }
  }
  
}
