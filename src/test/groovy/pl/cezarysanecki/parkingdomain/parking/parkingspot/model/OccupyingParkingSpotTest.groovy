package pl.cezarysanecki.parkingdomain.parking.parkingspot.model


import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.management.vehicle.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotFixture.emptyOpenParkingSpotWithCapacity

class OccupyingParkingSpotTest extends Specification {
  
  def "allow to occupy parking spot by vehicle"() {
    given:
      def vehicle = VehicleId.newOne()
      def vehicleSize = SpotUnits.of(2)
    
    and:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(4)
    
    when:
      def result = openParkingSpot.occupy(vehicle, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == openParkingSpot.parkingSpotId
        
        def parkingSpotOccupied = it.parkingSpotOccupied
        assert parkingSpotOccupied.parkingSpotId == openParkingSpot.parkingSpotId
        assert parkingSpotOccupied.vehicleId == vehicle
        assert parkingSpotOccupied.vehicleSize == vehicleSize
      }
  }
  
  def "allow to occupy parking spot by vehicle with full occupation"() {
    given:
      def vehicle = VehicleId.newOne()
      def vehicleSize = SpotUnits.of(4)
    
    and:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(vehicleSize.getValue())
    
    when:
      def result = openParkingSpot.occupy(vehicle, vehicleSize)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == openParkingSpot.parkingSpotId
        
        def parkingSpotOccupied = it.parkingSpotOccupied
        assert parkingSpotOccupied.parkingSpotId == openParkingSpot.parkingSpotId
        assert parkingSpotOccupied.vehicleId == vehicle
        assert parkingSpotOccupied.vehicleSize == vehicleSize
        
        def fullyOccupied = it.fullyOccupied.get()
        assert fullyOccupied.parkingSpotId == openParkingSpot.parkingSpotId
      }
  }
  
  def "reject to occupy parking spot by vehicle when it does not have enough space"() {
    given:
      def vehicle = VehicleId.newOne()
      def vehicleSize = SpotUnits.of(4)
    
    and:
      def openParkingSpot = emptyOpenParkingSpotWithCapacity(vehicleSize.getValue() - 1)
    
    when:
      def result = openParkingSpot.occupy(vehicle, vehicleSize)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == openParkingSpot.parkingSpotId
        assert it.vehicleId == vehicle
        assert it.reason == "there is not enough space for vehicle"
      }
  }
  
}
