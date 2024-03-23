package pl.cezarysanecki.parkingdomain.client.requestreservation.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static ClientReservationRequestsEvent.ReservationRequestFailed
import static ClientReservationsFixture.anyClientId
import static ClientReservationsFixture.noReservationRequests
import static ClientReservationsFixture.reservationRequestsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

class RequestingClientReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  
  LocalDateTime now = LocalDateTime.now()
  LocalDateTime properSinceReservation = now.plusDays(1)
  
  def "can make reservation for random parking spot"() {
    given:
      def clientReservations = noReservationRequests(clientId, now)
      def reservationSlot = new ReservationSlot(properSinceReservation, 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.reserve(reservationSlot)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationSlot == reservationSlot
        assert it.parkingSpotId.isEmpty()
      }
  }
  
  def "cannot make reservation for random parking spot when there is too many made reservations"() {
    given:
      def clientReservations = reservationRequestsWith(clientId, anyReservationId(), now)
      def reservationSlot = new ReservationSlot(properSinceReservation, 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.reserve(reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "cannot have more reservations"
      }
  }
  
  def "cannot make reservation for random parking spot when reservation is too soon"() {
    given:
      def tooSoonReservation = now.plusHours(2).plusMinutes(59)
    and:
      def clientReservations = reservationRequestsWith(clientId, now)
      def reservationSlot = new ReservationSlot(tooSoonReservation, 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.reserve(reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "reservation is too soon from now"
      }
  }
  
  def "can make reservation for chosen parking spot"() {
    given:
      def clientReservations = noReservationRequests(clientId, now)
      def reservationSlot = new ReservationSlot(properSinceReservation, 2)
      def parkingSpotId = anyParkingSpotId()
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.reserve(parkingSpotId, reservationSlot)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientId
        assert it.reservationSlot == reservationSlot
        assert it.parkingSpotId.get() == parkingSpotId
      }
  }
  
  def "cannot make reservation for chosen parking spot when there is too many made reservations"() {
    given:
      def clientReservations = reservationRequestsWith(clientId, anyReservationId(), now)
      def reservationSlot = new ReservationSlot(properSinceReservation, 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.reserve(anyParkingSpotId(), reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "cannot have more reservations"
      }
  }
  
  def "cannot make reservation for chosen parking spot when reservation is too soon"() {
    given:
      def tooSoonReservation = now.plusHours(2).plusMinutes(59)
    and:
      def clientReservations = reservationRequestsWith(clientId, now)
      def reservationSlot = new ReservationSlot(tooSoonReservation, 2)
    
    when:
      Either<ReservationRequestFailed, ReservationRequestCreated> result = clientReservations.reserve(anyParkingSpotId(), reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientId
        assert it.reason == "reservation is too soon from now"
      }
  }
  
}
