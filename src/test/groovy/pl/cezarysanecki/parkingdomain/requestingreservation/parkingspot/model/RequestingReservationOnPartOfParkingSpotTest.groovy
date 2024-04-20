package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model

import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutPlaceForReservationRequestsWithCapacity
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationsFixture.parkingSpotWithoutReservationRequests

class RequestingReservationOnPartOfParkingSpotTest extends Specification {
  
  def "allow to request reservation on part of parking spot"() {
    given:
      def parkingSpotReservationRequests = parkingSpotWithoutReservationRequests()
    and:
      def reservationId = ReservationId.newOne()
    and:
      def vehicleSize = VehicleSize.of(2)
    
    when:
      def result = parkingSpotReservationRequests.storeForPart(reservationId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpotReservationRequests.parkingSpotId
        assert it.reservationId == reservationId
        assert it.vehicleSize == vehicleSize
      }
  }
  
  def "reject requesting reservation on part of parking spot when there is not enough space"() {
    given:
      def parkingSpotReservations = parkingSpotWithoutPlaceForReservationRequestsWithCapacity(2)
    and:
      def reservationId = ReservationId.newOne()
    
    when:
      def result = parkingSpotReservations.storeForPart(reservationId, VehicleSize.of(3))
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotReservations.parkingSpotId
        assert it.reservationId == reservationId
        assert it.reason == "not enough parking spot space"
      }
  }
  
}
