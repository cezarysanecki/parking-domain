package pl.cezarysanecki.parkingdomain.reserving.parkingspot.model


import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsFixture.fullyReservedParkingSpotBy
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservations

class ReservingWholeParkingSpotTest extends Specification {
  
  def "allow to reserve whole parking spot"() {
    given:
      def parkingSpotReservations = noParkingSpotReservations()
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservations.reserveWhole(reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
      }
  }
  
  def "reject reserving whole parking spot when there is at least one reservation"() {
    given:
      def parkingSpotReservations = fullyReservedParkingSpotBy(ReservationId.newOne())
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservations.reserveWhole(reservationId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
        assert it.reason == "there are reservations for this parking spot"
      }
  }
  
}
