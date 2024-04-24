package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutPlaceForReservationRequests
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutReservationRequests

class CancellingParkingSpotReservationTest extends Specification {
  
  def "allow to cancel parking spot reservation request"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def parkingSpotReservationRequests = parkingSpotWithoutPlaceForReservationRequests(reservationId)
    
    when:
      def result = parkingSpotReservationRequests.cancel(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
        assert it.reservationId == reservationId
      }
  }
  
  def "reject cancelling reservation request for parking spot when this is no such request for that parking spot"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservationRequests.cancel(reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
        assert it.reservationId == reservationId
        assert it.reason == "there is no such reservation request on that parking spot"
      }
  }
  
}
