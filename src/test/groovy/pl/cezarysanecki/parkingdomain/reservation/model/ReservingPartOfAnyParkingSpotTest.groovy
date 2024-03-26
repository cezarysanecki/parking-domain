package pl.cezarysanecki.parkingdomain.reservation.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit
import spock.lang.Specification

import static ParkingSpotReservationsFixture.anyReservationId
import static ParkingSpotReservationsFixture.emptyParkingSpotReservations
import static ParkingSpotReservationsFixture.individual
import static ParkingSpotReservationsFixture.parkingSpotReservationsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationForPartOfParkingSpotMade

class ReservingPartOfAnyParkingSpotTest extends Specification {
  
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
      def dayPartReservations = new ParkingSpotReservation(individual(anyReservationId()), ReservationPeriod.wholeDay())
    
    and:
      def parkingSpotReservations = parkingSpotReservationsWith(parkingSpotId, dayPartReservations)
    
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
