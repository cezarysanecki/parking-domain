package pl.cezarysanecki.parkingdomain.reservation.schedule.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit
import spock.lang.Specification

import static ParkingSpotReservationsEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.*
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsFixture.emptyParkingSpotReservations
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsFixture.individual
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsFixture.parkingSpotReservationsWith

class ReservingAnyPartOfParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  def "can reserve any part of parking spot if there are no other reservations"() {
    given:
      def reservationPeriod = ReservationPeriod.evening()
    and:
      def vehicleSizeUnit = VehicleSizeUnit.of(2)
      def parkingSpotReservations = emptyParkingSpotReservations(parkingSpotId)
      
    when:
      Either<ReservationFailed, ReservationForPartOfParkingSpotMade> result = parkingSpotReservations.reservePart(reservationId, reservationPeriod, vehicleSizeUnit)
    
    then:
      result.isRight()
      result.get().with {
        assert it.reservationId == reservationId
        assert it.reservationPeriod == reservationPeriod
        assert it.parkingSpotId == parkingSpotId
        assert it.vehicleSizeUnit == vehicleSizeUnit
      }
  }
  
  def "cannot reserve any part of parking spot if there is individual reservation"() {
    given:
      def reservationPeriod = ReservationPeriod.evening()
    and:
      def dayPart = ReservationPeriod.DayPart.Evening
      def dayPartReservations = new DayPartReservations(dayPart, Set.of(individual(anyReservationId())))
    
    and:
      def parkingSpotReservations = parkingSpotReservationsWith(parkingSpotId, dayPart, dayPartReservations)
    
    when:
      Either<ReservationFailed, ReservationForPartOfParkingSpotMade> result = parkingSpotReservations.reservePart(reservationId, reservationPeriod, VehicleSizeUnit.of(2))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.reservationId == reservationId
        assert it.reservationPeriod == reservationPeriod
        assert it.parkingSpotId == parkingSpotId
        assert it.reason == "there is an individual reservation for that period"
      }
  }
  
}
