package pl.cezarysanecki.parkingdomain.reservation.application

import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.*
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationScheduleFixture.emptyReservationSchedule

class MakingParkingSlotReservationTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
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
          new ReserveParkingSpotCommand(parkingSpotId, Set.of(vehicleWith(2)), new ReservationSlot(now, 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should successfully reserve any parking spot'() {
    given:
      MakingParkingSlotReservation makingParkingSlotReservation = new MakingParkingSlotReservation(repository)
    and:
      def now = LocalDateTime.now()
    and:
      def reservationSlot = new ReservationSlot(now, 3)
    and:
      anyPersisted(reservationSlot, now)
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveAnyParkingSpotCommand(Set.of(vehicleWith(2)), reservationSlot))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject reserving parking spot when vehicle is too big'() {
    given:
      MakingParkingSlotReservation makingParkingSlotReservation = new MakingParkingSlotReservation(repository)
    and:
      def now = LocalDateTime.now()
    and:
      persisted(emptyReservationSchedule(parkingSpotId, now))
    
    when:
      def result = makingParkingSlotReservation.reserve(
          new ReserveParkingSpotCommand(parkingSpotId, Set.of(vehicleWith(5)), new ReservationSlot(now, 3)))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  ReservationSchedule persisted(ReservationSchedule reservationSchedule) {
    repository.findBy(reservationSchedule.parkingSpotId) >> reservationSchedule
    repository.publish(_ as ReservationEvent) >> reservationSchedule
    return reservationSchedule
  }
  
  ReservationSchedule anyPersisted(ReservationSlot reservationSlot, LocalDateTime now) {
    def reservationSchedule = new ReservationSchedule(parkingSpotId, Reservations.none(), true, now)
    repository.findFreeFor(reservationSlot) >> reservationSchedule
    repository.publish(_ as ReservationEvent) >> reservationSchedule
    return reservationSchedule
  }
  
}
