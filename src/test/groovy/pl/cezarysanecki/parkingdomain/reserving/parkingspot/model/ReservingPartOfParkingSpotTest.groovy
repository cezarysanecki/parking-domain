package pl.cezarysanecki.parkingdomain.reserving.parkingspot.model

import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservations
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsFixture.noParkingSpotReservationsWithCapacity

class ReservingPartOfParkingSpotTest extends Specification {
  
  def "allow to reserve part of parking spot"() {
    given:
      def parkingSpotReservations = noParkingSpotReservations()
    and:
      def reservationId = ReservationId.newOne()
    and:
      def vehicleSize = VehicleSize.of(2)
    
    when:
      def result = parkingSpotReservations.reservePart(reservationId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
        assert it.vehicleSize == vehicleSize
      }
  }
  
  def "reject reserving part of parking spot when there is not enough space"() {
    given:
      def parkingSpotReservations = noParkingSpotReservationsWithCapacity(2)
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservations.reservePart(reservationId, VehicleSize.of(3))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
        assert it.reason == "not to many parking spot space"
      }
  }
  
}
