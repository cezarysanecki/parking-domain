package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.outOfOrderParkingSpot
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class ParkingOnParkingSpotTest extends Specification {
  
  def "cannot park on out of order open parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = outOfOrderParkingSpot()
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("parking on out of order parking spot is forbidden")
      }
  }
  
  def "cannot park on open parking spot by too big vehicle"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(2)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("not enough space on parking spot")
      }
  }
  
  def "vehicle can park on open parking spot if there is enough space"() {
    given:
      ParkingSpot openParkingSpot = emptyParkingSpotWith(4)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = openParkingSpot.park(vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == openParkingSpot.parkingSpotId
        assert vehicleParked.vehicle == vehicle
      }
  }
  
  def "vehicle can park fully occupying open parking spot"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
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
  
}
