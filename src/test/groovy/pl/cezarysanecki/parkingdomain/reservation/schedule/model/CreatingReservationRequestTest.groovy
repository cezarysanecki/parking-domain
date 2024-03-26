package pl.cezarysanecki.parkingdomain.reservation.schedule.model

import io.vavr.control.Either
import spock.lang.Specification

import java.time.LocalDateTime

import static ParkingSpotReservationsEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static ReservationScheduleFixture.anyReservationId
import static ReservationScheduleFixture.emptyReservationSchedule
import static ReservationScheduleFixture.occupiedReservationSchedule
import static ReservationScheduleFixture.reservationScheduleWith
import static ReservationScheduleFixture.reservationWith

class CreatingReservationRequestTest extends Specification {
  
  ReservationId reservationId = anyReservationId()
  
  def "can reserve when there are no other reservations"() {
    given:
      def clientId = anyClientId()
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSchedule = emptyReservationSchedule(now)
    and:
      def reservationSlot = new ReservationSlot(now, 2)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(clientId, reservationSlot, reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.reservationSlot == reservationSlot
        assert it.clientId == clientId
        assert it.reservationId == reservationId
      }
  }
  
  def "can reserve when reservations does not intersect"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSlot = new ReservationSlot(now, 2)
    and:
      def reservationSchedule = reservationScheduleWith(now, reservationWith(reservationSlot.moveBy(12), anyClientId()))
    and:
      def clientId = anyClientId()
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(clientId, reservationSlot, reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.reservationSlot == reservationSlot
        assert it.clientId == clientId
        assert it.reservationId == reservationId
      }
  }
  
  def "can reserve when since date is not too early for occupied parking spot"() {
    given:
      def clientId = anyClientId()
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSchedule = occupiedReservationSchedule(now)
    and:
      def reservationSlot = new ReservationSlot(now.plusHours(3), 2)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(clientId, reservationSlot, reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.reservationSlot == reservationSlot
        assert it.clientId == clientId
        assert it.reservationId == reservationId
      }
  }
  
  def "cannot reserve parking spot in the same time as other reservation"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSlot = new ReservationSlot(now, 2)
      def reservation = new Reservation(anyReservationId(), reservationSlot, anyClientId())
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(anyClientId(), reservationSlot, reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.clientId == clientId
        assert it.reservationId == reservationId
        assert it.getReason().contains("there is another reservation in that time")
      }
  }
  
  def "cannot reserve parking spot for the same client"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def clientId = anyClientId()
    and:
      def reservationSlot = new ReservationSlot(now, 2)
      def reservation = reservationWith(reservationSlot, clientId)
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(clientId, reservationSlot.moveBy(2), reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.clientId == clientId
        assert it.reservationId == reservationId
        assert it.getReason().contains("it is already reserved for this client")
      }
  }
  
  def "cannot reserve parking spot too early when parking spot is occupied"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSchedule = occupiedReservationSchedule(now)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(
          anyClientId(), new ReservationSlot(now.plusHours(2), 2), reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.clientId == clientId
        assert it.reservationId == reservationId
        assert it.getReason().contains("need to give some time to free parking spot")
      }
  }
  
}
