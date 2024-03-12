package pl.cezarysanecki.parkingdomain.reservation.model

import io.vavr.control.Either
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancellationFailed
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancelled
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationScheduleFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationScheduleFixture.reservationScheduleWith

class CancellingReservationTest extends Specification {
  
  def "allow to cancel reservation"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationId = anyReservationId()
      def reservation = new Reservation(reservationId, new ReservationSlot(now.plusMinutes(60), 3), Set.of(vehicleWith(1)))
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationCancellationFailed, ReservationCancelled> result = reservationSchedule.cancel(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.reservationId == reservationId
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
      }
  }
  
  def "cannot cancel reservation that not exists"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservation = new Reservation(anyReservationId(), new ReservationSlot(now, 3), Set.of(vehicleWith(1)))
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationCancellationFailed, ReservationCancelled> result = reservationSchedule.cancel(anyReservationId())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.getReason().contains("there is no such reservation")
      }
  }
  
  def "cannot cancel reservation when it is too late"() {
    given:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationId = anyReservationId()
      def reservation = new Reservation(reservationId, new ReservationSlot(now.plusMinutes(59), 3), Set.of(vehicleWith(1)))
    and:
      def reservationSchedule = reservationScheduleWith(now, reservation)
    
    when:
      Either<ReservationCancellationFailed, ReservationCancelled> result = reservationSchedule.cancel(reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == reservationSchedule.parkingSpotId
        assert it.getReason().contains("it is too late to cancel reservation")
      }
  }
  
}
