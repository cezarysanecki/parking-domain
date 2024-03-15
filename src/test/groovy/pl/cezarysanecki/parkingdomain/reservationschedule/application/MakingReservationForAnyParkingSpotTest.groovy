package pl.cezarysanecki.parkingdomain.reservationschedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import pl.cezarysanecki.parkingdomain.reservationschedule.model.Reservations
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationScheduleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationWith

class MakingReservationForAnyParkingSpotTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  ReservationSchedules repository = Mock()
  
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
      makingParkingSlotReservation.handle(new ReservationRequestCreated(clientId, reservationSlot, Option.none()))
    
    then:
      1 * repository.publish(_ as ReservationMade)
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
      makingParkingSlotReservation.handle(new ReservationRequestCreated(clientId, reservationSlot, Option.none()))
    
    then:
      1 * repository.publish(_ as ReservationFailed)
  }
  
  ReservationSchedule persistedEmpty(ReservationSlot reservationSlot, LocalDateTime now) {
    def reservationSchedule = new ReservationSchedule(parkingSpotId, Reservations.none(), true, now)
    repository.findFreeFor(reservationSlot) >> Option.of(reservationSchedule)
    return reservationSchedule
  }
  
  ReservationSchedule persisted(ReservationSlot reservationSlot, LocalDateTime now) {
    def reservationSchedule = reservationScheduleWith(parkingSpotId, now, reservationWith(reservationSlot, clientId))
    repository.findFreeFor(reservationSlot) >> Option.of(reservationSchedule)
    return reservationSchedule
  }
  
}
