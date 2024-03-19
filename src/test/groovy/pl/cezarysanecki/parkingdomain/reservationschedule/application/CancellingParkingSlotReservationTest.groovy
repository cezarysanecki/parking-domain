package pl.cezarysanecki.parkingdomain.reservationschedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.reservationschedule.model.Reservation
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationScheduleWith

class CancellingParkingSlotReservationTest extends Specification {
  
  ClientId clientId = anyClientId()
  
  ReservationSchedules repository = Stub()
  
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
    repository.publish(_ as ReservationScheduleEvent) >> reservationSchedule
    return reservationSchedule
  }
  
  ReservationId unknownReservationSchedule(ReservationId reservationId) {
    repository.findBy(reservationId) >> Option.none()
    return reservationId
  }
  
}
