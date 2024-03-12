package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.collection.List
import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft
import static ParkingSpotFixture.*

class ReleasingParkingSpotTest extends Specification {
  
  def "can release parked vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = parkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeft> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicle == vehicle
      }
  }
  
  def "release all parked vehicle"() {
    given:
      ParkingSpot parkingSpot = parkingSpotWith([vehicleWith(1), vehicleWith(1)])
    
    when:
      List<VehicleLeft> result = parkingSpot.releaseAll()
    
    then:
      result.size() == 2
  }
  
  def "can release out of order parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = outOfOrderParkingSpot()
    
    when:
      Either<ReleasingFailed, VehicleLeft> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicle == vehicle
      }
  }
  
  def "vehicle cannot be release from parking spot if it is not on this spot"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ReleasingFailed, VehicleLeft> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
      }
  }
  
}
