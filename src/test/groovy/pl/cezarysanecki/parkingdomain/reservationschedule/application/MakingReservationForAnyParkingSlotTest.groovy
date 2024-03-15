package pl.cezarysanecki.parkingdomain.reservationschedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import pl.cezarysanecki.parkingdomain.reservationschedule.model.Reservations
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationScheduleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationWith

class MakingReservationForAnyParkingSlotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ClientId clientId = anyClientId()
  
  ReservationSchedules repository = Stub()
  
  def 'should successfully reserve any parking spot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def now = LocalDateTime.now()
    and:
      def reservationSlot = new ReservationSlot(now, 3)
    and:
      persistedEmpty(reservationSlot, now)
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveAnyParkingSpotCommand(clientId, reservationSlot))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject reserving any parking spot when there is reservation on that slot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def now = LocalDateTime.now()
    and:
      def reservationSlot = new ReservationSlot(now, 3)
    and:
      persisted(reservationSlot, now)
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveAnyParkingSpotCommand(clientId, reservationSlot))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if reservation schedule does not exist'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      unknownReservationSchedule()
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveAnyParkingSpotCommand(clientId, new ReservationSlot(LocalDateTime.now(), 3)))
    
    then:
      result.isFailure()
  }
  
  ReservationSchedule persistedEmpty(ReservationSlot reservationSlot, LocalDateTime now) {
    def reservationSchedule = new ReservationSchedule(parkingSpotId, Reservations.none(), true, now)
    repository.findFreeFor(reservationSlot) >> Option.of(reservationSchedule)
    repository.publish(_ as ReservationScheduleEvent) >> reservationSchedule
    return reservationSchedule
  }
  
  ReservationSchedule persisted(ReservationSlot reservationSlot, LocalDateTime now) {
    def reservationSchedule = reservationScheduleWith(parkingSpotId, now, reservationWith(reservationSlot, clientId))
    repository.findFreeFor(reservationSlot) >> Option.of(reservationSchedule)
    repository.publish(_ as ReservationScheduleEvent) >> reservationSchedule
    return reservationSchedule
  }
  
  ParkingSpotId unknownReservationSchedule() {
    repository.findBy(parkingSpotId) >> Option.none()
    return parkingSpotId
  }
  
}
