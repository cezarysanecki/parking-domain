package pl.cezarysanecki.parkingdomain.reservation.model

import io.vavr.control.Either
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationScheduleFixture.*

class MakingReservationTest extends Specification {
  
  def "can reserve when there is no other reservation"() {
    given:
      def reservationSchedule = emptyReservationSchedule(LocalDateTime.now())
    and:
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(Set.of(vehicleWith(1)), reservationSlot)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
      }
  }
  
  def "can reserve when reservations does not intersect"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSlot = new ReservationSlot(now, 2)
      def reservation = new Reservation(anyReservationId(), reservationSlot, Set.of(vehicleWith(1)))
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(Set.of(vehicleWith(1)), reservationSlot.moveBy(3))
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
      }
  }
  
  def "can reserve when since date is not too early for occupied parking spot"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSchedule = occupiedReservationSchedule(now)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(
          Set.of(vehicleWith(1)), new ReservationSlot(now.plusHours(3), 2))
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
      }
  }
  
  def "cannot reserve parking spot for too many vehicles"() {
    given:
      def reservationSchedule = emptyReservationSchedule(LocalDateTime.now())
    and:
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(Set.of(vehicleWith(5)), reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.getReason().contains("cannot accommodate requested vehicles because of space")
      }
  }
  
  def "cannot reserve parking spot in the same time as other reservation"() {
    given:
      def reservationSlot = new ReservationSlot(LocalDateTime.now(), 2)
      def reservation = new Reservation(anyReservationId(), reservationSlot, Set.of(vehicleWith(1)))
    and:
      def reservationSchedule = reservationScheduleWith(LocalDateTime.now(), reservation)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(Set.of(vehicleWith(1)), reservationSlot)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.getReason().contains("there is another reservation in that time")
      }
  }
  
  def "cannot reserve parking spot fot the same vehicle"() {
    given:
      def now = LocalDateTime.now()
    and:
      def vehicle = vehicleWith(1)
    and:
      def reservationSlot = new ReservationSlot(now, 2)
      def reservation = new Reservation(anyReservationId(), reservationSlot, Set.of(vehicle))
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(Set.of(vehicle), reservationSlot.moveBy(2))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.getReason().contains("it is already reserved for one of vehicles")
      }
  }
  
  def "cannot reserve parking spot too early when parking spot is occupied"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSchedule = occupiedReservationSchedule(now)
    
    when:
      Either<ReservationFailed, ReservationMade> result = reservationSchedule.reserve(
          Set.of(vehicleWith(1)), new ReservationSlot(now.plusHours(2), 2))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.getReason().contains("need to give some time to free parking spot")
      }
  }
  
}
