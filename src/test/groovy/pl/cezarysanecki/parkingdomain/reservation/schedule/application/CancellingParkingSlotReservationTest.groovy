package pl.cezarysanecki.parkingdomain.reservation.schedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.Reservation
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId

import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsRepository

import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.reservationScheduleWith

class CancellingParkingSlotReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  
  ParkingSpotReservationsRepository repository = Stub()
  
  def 'should successfully cancel reservation'() {
    given:
      CancellingReservation cancellingReservation = new CancellingReservation(repository)
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationId = anyReservationId()
      def reservation = new Reservation(reservationId, new ReservationSlot(now.plusMinutes(60), 3), clientId)
    and:
      persisted(reservationId, reservationScheduleWith(now, reservation))
    
    when:
      def result = cancellingReservation.cancel(new CancelReservationCommand(reservationId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject cancelling reservation when its to late'() {
    given:
      CancellingReservation cancellingReservation = new CancellingReservation(repository)
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationId = anyReservationId()
      def reservation = new Reservation(reservationId, new ReservationSlot(now.plusMinutes(30), 3), clientId)
    and:
      persisted(reservationId, reservationScheduleWith(now, reservation))
    
    when:
      def result = cancellingReservation.cancel(new CancelReservationCommand(reservationId))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if reservation schedule does not exists'() {
    given:
      CancellingReservation cancellingReservation = new CancellingReservation(repository)
    and:
      def reservationId = anyReservationId()
    and:
      unknownReservationSchedule(reservationId)
    
    when:
      def result = cancellingReservation.cancel(new CancelReservationCommand(reservationId))
    
    then:
      result.isFailure()
  }
  
  ReservationSchedule persisted(ReservationId reservationId, ReservationSchedule reservationSchedule) {
    repository.findBy(reservationId) >> Option.of(reservationSchedule)
    repository.publish(_ as ParkingSpotReservationsEvent) >> reservationSchedule
    return reservationSchedule
  }
  
  ReservationId unknownReservationSchedule(ReservationId reservationId) {
    repository.findBy(reservationId) >> Option.none()
    return reservationId
  }
  
}
