package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model


import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutPlaceForReservationRequests
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutReservationRequests

class RequestingReservationOnWholeParkingSpotTest extends Specification {
  
  def "allow to request reservation on whole parking spot"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservationRequests.storeForWhole(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
        assert it.reservationId == reservationId
      }
  }
  
  def "reject requesting reservation on whole parking spot when there is at least one request"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutPlaceForReservationRequests(ReservationId.newOne())
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservationRequests.storeForWhole(reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
        assert it.reservationId == reservationId
        assert it.reason == "there are reservation requests for this parking spot"
      }
  }
  
}
