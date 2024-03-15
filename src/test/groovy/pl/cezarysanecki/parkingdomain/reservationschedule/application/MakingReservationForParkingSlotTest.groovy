package pl.cezarysanecki.parkingdomain.reservationschedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.emptyReservationSchedule
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationScheduleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationWith

class MakingReservationForParkingSlotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ClientId clientId = anyClientId()
  
  ReservationSchedules repository = Stub()
  
  def 'should successfully reserve parking spot'() {
    given:
      MakingParkingSlotReservation makingParkingSlotReservation = new MakingParkingSlotReservation(repository)
    and:
      def now = LocalDateTime.now()
    and:
      persisted(emptyReservationSchedule(parkingSpotId, now))
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveParkingSpotCommand(parkingSpotId, clientId, new ReservationSlot(now, 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject reserving parking spot when there is reservation on that slot'() {
    given:
      MakingParkingSlotReservation makingParkingSlotReservation = new MakingParkingSlotReservation(repository)
    and:
      def now = LocalDateTime.now()
    and:
      def reservationSlot = new ReservationSlot(now, 3)
    and:
      persisted(reservationScheduleWith(parkingSpotId, now, reservationWith(reservationSlot, clientId)))
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveParkingSpotCommand(parkingSpotId, clientId, reservationSlot))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if reservation schedule does not exist'() {
    given:
      MakingParkingSlotReservation makingParkingSlotReservation = new MakingParkingSlotReservation(repository)
    and:
      unknownReservationSchedule()
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveParkingSpotCommand(parkingSpotId, clientId, new ReservationSlot(LocalDateTime.now(), 3)))
    
    then:
      result.isFailure()
  }
  
  ReservationSchedule persisted(ReservationSchedule reservationSchedule) {
    repository.findBy(reservationSchedule.parkingSpotId) >> Option.of(reservationSchedule)
    repository.publish(_ as ReservationScheduleEvent) >> reservationSchedule
    return reservationSchedule
  }
  
  ParkingSpotId unknownReservationSchedule() {
    repository.findBy(parkingSpotId) >> Option.none()
    return parkingSpotId
  }
  
}
