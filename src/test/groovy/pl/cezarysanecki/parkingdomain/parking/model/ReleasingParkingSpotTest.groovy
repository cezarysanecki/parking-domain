package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.collection.List
import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.outOfOrderParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.parkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class ReleasingParkingSpotTest extends Specification {
  
  def "vehicle cannot release occupied parking spot if it is not on this spot"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.driveAway(vehicle.vehicleId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleIds == List.ofAll([vehicle.vehicleId])
        assert it.reason == "vehicle not park on this spot"
      }
  }
  
  def "vehicle can release occupied parking spot if it is on it"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = parkingSpotWith([vehicle, vehicleWith(1)])
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.driveAway(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "can release out of order occupied parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = outOfOrderParkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.driveAway(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "vehicle can release occupied parking spot and free it up"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = parkingSpotWith([vehicle])
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.driveAway(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
        
        CompletelyFreedUp completelyFreedUp = it.completelyFreedUps.get()
        assert completelyFreedUp.parkingSpotId == parkingSpot.parkingSpotId
      }
  }
  
}
