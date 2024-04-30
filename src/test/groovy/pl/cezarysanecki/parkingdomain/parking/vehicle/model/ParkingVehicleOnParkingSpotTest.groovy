package pl.cezarysanecki.parkingdomain.parking.vehicle.model


import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class ParkingVehicleOnParkingSpotTest extends Specification {
  
  def "allow to park vehicle on parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def vehicle = notParkedVehicle()
    
    when:
      def result = vehicle.parkOn(parkingSpotId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
        assert it.vehicleSize == vehicle.vehicleInformation.vehicleSize
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "fail to park vehicle on parking spot when it is already parked"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def vehicle = parkedVehicleOn(ParkingSpotId.newOne())
    
    when:
      def result = vehicle.parkOn(parkingSpotId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
        assert it.reason == "vehicle is already parked"
      }
  }
  
  def "allow to park vehicle on reserved parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def reservationId = ReservationId.newOne()
    and:
      def vehicle = notParkedVehicle()
    
    when:
      def result = vehicle.parkOnUsing(parkingSpotId, reservationId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
        
        def vehicleParked = it.vehicleParked
        assert vehicleParked.vehicleSize == vehicle.vehicleInformation.vehicleSize
        assert vehicleParked.parkingSpotId == parkingSpotId
        
        def fulfilledReservation = it.fulfilledReservation.get()
        fulfilledReservation.reservationId == reservationId
      }
  }
  
}
