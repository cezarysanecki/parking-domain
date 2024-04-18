package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model

import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.fullyReservedParkingSpotBy
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservations

class CancellingParkingSpotReservationTest extends Specification {
  
  def "allow to cancel parking spot reservation"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def parkingSpotReservations = fullyReservedParkingSpotBy(reservationId)
    
    when:
      def result = parkingSpotReservations.cancel(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
      }
  }
  
  def "reject cancelling reservation for parking spot when this is no such reservation for that parking spot"() {
    given:
      def parkingSpotReservations = noParkingSpotReservations()
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservations.cancel(reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
        assert it.reason == "there is no such reservation on that parking spot"
      }
  }
  
}
