package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.*
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWithSize

class ParkingVehicleTest extends Specification {
  
  def "vehicle can park if there is enough space"() {
    given:
      def parkingSpot = emptyParkingSpotWith(4)
    and:
      def vehicle = vehicleWithSize(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleParked.vehicle == vehicle
      }
  }
  
  def "vehicle can park occupying fully parking spot"() {
    given:
      def parkingSpot = emptyParkingSpotWith(1)
    and:
      def vehicle = vehicleWithSize(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleParked.vehicle == vehicle
        
        FullyOccupied fullyOccupied = it.fullyOccupied.get()
        assert fullyOccupied.parkingSpotId == parkingSpot.parkingSpotId
      }
  }
  
  def "vehicle cannot park is too big for parking spot"() {
    given:
      def parkingSpot = emptyParkingSpotWith(1)
    and:
      def vehicle = vehicleWithSize(2)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
      }
  }
  
}
