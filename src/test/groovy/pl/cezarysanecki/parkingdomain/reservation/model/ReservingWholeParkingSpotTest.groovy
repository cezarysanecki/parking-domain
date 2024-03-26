package pl.cezarysanecki.parkingdomain.reservation.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import spock.lang.Specification

import static ParkingSpotReservationsEvent.ReservationFailed
import static ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade
import static ParkingSpotReservationsFixture.anyReservationId
import static ParkingSpotReservationsFixture.collective
import static ParkingSpotReservationsFixture.emptyParkingSpotReservations
import static ParkingSpotReservationsFixture.parkingSpotReservationsWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId

class ReservingWholeParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  def "can reserve whole parking spot if there are no other reservations"() {
    given:
      def reservationPeriod = ReservationPeriod.evening()
    and:
      def parkingSpotReservations = emptyParkingSpotReservations(parkingSpotId)
    
    when:
      Either<ReservationFailed, ReservationForWholeParkingSpotMade> result = parkingSpotReservations.reserveWhole(reservationId, reservationPeriod)
    
    then:
      result.isRight()
      result.get().with {
        assert it.reservationId == reservationId
        assert it.reservationPeriod == reservationPeriod
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "cannot reserve whole parking spot if there is any reservation"() {
    given:
      def reservationPeriod = ReservationPeriod.evening()
    and:
      def dayPartReservations = new ParkingSpotReservation(ReservationPeriod.morning(), collective(anyReservationId()))
    
    and:
      def parkingSpotReservations = parkingSpotReservationsWith(parkingSpotId, dayPartReservations)
    
    when:
      Either<ReservationFailed, ReservationForWholeParkingSpotMade> result = parkingSpotReservations.reserveWhole(reservationId, reservationPeriod)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.reservationId == reservationId
        assert it.reservationPeriod == reservationPeriod
        assert it.parkingSpotId == parkingSpotId
        assert it.reason == "there is any reservation for that period"
      }
  }
  
}
