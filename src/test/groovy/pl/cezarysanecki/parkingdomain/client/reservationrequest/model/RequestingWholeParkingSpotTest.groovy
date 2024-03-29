package pl.cezarysanecki.parkingdomain.client.reservationrequest.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ChosenParkingSpotReservationRequested
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestFailed
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import spock.lang.Specification

import java.time.LocalDateTime

import static ClientReservationRequestsFixture.anyClientId
import static ClientReservationRequestsFixture.noReservationRequests
import static ClientReservationRequestsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId

class RequestingWholeParkingSpotTest extends Specification {
  
  ClientId clientId = anyClientId()
  
  LocalDateTime now = LocalDateTime.now()
  
  def "can make reservation for whole parking spot"() {
    given:
      def clientReservationRequests = noReservationRequests(clientId)
    and:
      def reservationPeriod = ReservationPeriod.morning()
      def parkingSpotId = anyParkingSpotId()
    
    when:
      Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> result = clientReservationRequests.createRequest(reservationPeriod, parkingSpotId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationPeriod == reservationPeriod
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "cannot make reservation for whole parking spot when there is too many made reservations"() {
    given:
      def clientReservations = reservationRequestsWith(clientId, anyReservationId())
    
    when:
      Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> result = clientReservations.createRequest(ReservationPeriod.morning(), anyParkingSpotId())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "cannot have more reservations"
      }
  }
  
}
