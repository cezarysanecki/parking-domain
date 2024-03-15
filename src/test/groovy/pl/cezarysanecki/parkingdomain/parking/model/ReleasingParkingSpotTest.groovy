package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.collection.List
import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.*
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.*

class ReleasingParkingSpotTest extends Specification {
  
  def "can release parked vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      CommonParkingSpot parkingSpot = parkingSpotWith([vehicle, vehicleWith(1)])
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehicleLeft
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "can release parked vehicle and free it"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      CommonParkingSpot parkingSpot = parkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehicleLeft
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
        
        CompletelyFreedUp completelyFreedUp = it.completelyFreedUps.get()
        assert completelyFreedUp.parkingSpotId == parkingSpot.parkingSpotId
      }
  }
  
  def "can release out of order parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      CommonParkingSpot parkingSpot = outOfOrderParkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehicleLeft
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "release all parked vehicle"() {
    given:
      CommonParkingSpot parkingSpot = parkingSpotWith([vehicleWith(1), vehicleWith(1)])
    
    when:
      List<VehicleLeft> result = parkingSpot.releaseAll()
    
    then:
      result.size() == 2
  }
  
  def "vehicle cannot be release from parking spot if it is not on this spot"() {
    given:
      CommonParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason == "vehicle not park on this spot"
      }
  }
  
}
