package pl.cezarysanecki.parkingdomain.reservationschedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.emptyReservationSchedule
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationScheduleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.reservationWith

class MakingReservationForParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ClientId clientId = anyClientId()
  
  ReservationSchedules repository = Mock()
  
  def 'should successfully reserve parking spot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSlot = new ReservationSlot(now, 3)
    and:
      persisted(emptyReservationSchedule(parkingSpotId, now))
    
    when:
      makingParkingSlotReservation.handle(new ReservationRequestCreated(clientId, reservationSlot, Option.of(parkingSpotId)))
    
    then:
      1 * repository.publish(_ as ReservationMade)
  }
  
  def 'should reject reserving parking spot when there is reservation on that slot'() {
    given:
      MakingReservationEventListener makingParkingSlotReservation = new MakingReservationEventListener(repository)
    and:
      def now = LocalDateTime.of(2024, 10, 10, 10, 0)
    and:
      def reservationSlot = new ReservationSlot(now, 3)
    and:
      persisted(reservationScheduleWith(parkingSpotId, now, reservationWith(reservationSlot, clientId)))
    
    when:
      makingParkingSlotReservation.handle(new ReservationRequestCreated(clientId, reservationSlot, Option.of(parkingSpotId)))
    
    then:
      1 * repository.publish(_ as ReservationFailed)
  }
  
  ReservationSchedule persisted(ReservationSchedule reservationSchedule) {
    repository.findBy(reservationSchedule.parkingSpotId) >> Option.of(reservationSchedule)
    return reservationSchedule
  }
  
}
