package pl.cezarysanecki.parkingdomain.reservation.schedule.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId

import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsRepository

import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.emptyReservationSchedule
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.reservationScheduleWith
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.reservationWith

class CreatingReservationRequestForParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ClientId clientId = anyClientId()
  
  ParkingSpotReservationsRepository repository = Mock()
  
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
