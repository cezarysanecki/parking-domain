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
  
  def "can release parked vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      OpenParkingSpot parkingSpot = parkingSpotWith([vehicle, vehicleWith(1)])
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "can release parked vehicle and free it"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      OpenParkingSpot parkingSpot = parkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
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
      OpenParkingSpot parkingSpot = outOfOrderParkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
        assert vehicleLeft.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "release all parked vehicle"() {
    given:
      OpenParkingSpot parkingSpot = parkingSpotWith([vehicleWith(1), vehicleWith(1)])
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseAll()
    
    then:
      result.isRight()
      result.get().with {
        List<VehicleLeft> vehiclesLeft = it.vehiclesLeft
        assert vehiclesLeft.size() == 2
        
        CompletelyFreedUp completelyFreedUp = it.completelyFreedUps.get()
        assert completelyFreedUp.parkingSpotId == parkingSpotId
      }
  }
  
  def "reject to release empty parking spot"() {
    given:
      OpenParkingSpot parkingSpot = emptyParkingSpotWith(4)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseAll()
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "vehicle cannot be release from parking spot if it is not on this spot"() {
    given:
      OpenParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleIds == List.ofAll([vehicle.vehicleId])
        assert it.reason == "vehicle not park on this spot"
      }
  }
  
}
