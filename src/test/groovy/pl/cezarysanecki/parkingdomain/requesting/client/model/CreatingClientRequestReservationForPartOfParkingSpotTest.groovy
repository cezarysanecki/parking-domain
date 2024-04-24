package pl.cezarysanecki.parkingdomain.requesting.client.model

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithNoRequests

class CreatingClientRequestReservationForPartOfParkingSpotTest extends Specification {
  
  def "allow to create client reservation request for part of parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def vehicleSize = VehicleSize.of(2)
    and:
      def clientReservationRequests = clientWithNoRequests()
      
    when:
      def result = clientReservationRequests.createRequest(parkingSpotId, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.clientId == clientReservationRequests.clientId
        assert it.parkingSpotId == parkingSpotId
        assert it.vehicleSize == vehicleSize
      }
  }
  
  def "reject creating client reservation request for part of parking spot when limit of request is reached"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def vehicleSize = VehicleSize.of(2)
    and:
      def clientReservationRequests = clientWithRequest(ReservationId.newOne())
      
    when:
      def result = clientReservationRequests.createRequest(parkingSpotId, vehicleSize)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.clientId == clientReservationRequests.clientId
        assert it.reason == "client has too many requests"
      }
  }
  
}
