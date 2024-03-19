package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.collection.List
import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.parking.model.releasing.OccupiedParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.releasing.OccupiedParkingSpotFactory
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.outOfOrderParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.parkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class ReleasingOccupiedParkingSpotTest extends Specification {
  
  def "vehicle cannot release occupied parking spot if it is not on this spot"() {
    given:
      OccupiedParkingSpot parkingSpot = OccupiedParkingSpotFactory.create(emptyParkingSpotWith(1))
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.base.parkingSpotId
        assert it.vehicleIds == List.ofAll([vehicle.vehicleId])
        assert it.reason == "vehicle not park on this spot"
      }
  }
  
  def "vehicle can release occupied parking spot if it is on it"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      OccupiedParkingSpot parkingSpot = OccupiedParkingSpotFactory.create(parkingSpotWith([vehicle, vehicleWith(1)]))
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
        assert vehicleLeft.parkingSpotId == parkingSpot.base.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "can release out of order occupied parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      OccupiedParkingSpot parkingSpot = OccupiedParkingSpotFactory.create(outOfOrderParkingSpotWith(vehicle))
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
        assert vehicleLeft.parkingSpotId == parkingSpot.base.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
      }
  }
  
  def "vehicle can release occupied parking spot and free it up"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      OccupiedParkingSpot parkingSpot = OccupiedParkingSpotFactory.create(parkingSpotWith([vehicle]))
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseBy(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        VehicleLeft vehicleLeft = it.vehiclesLeft.first()
        assert vehicleLeft.parkingSpotId == parkingSpot.base.parkingSpotId
        assert vehicleLeft.vehicle == vehicle
        
        CompletelyFreedUp completelyFreedUp = it.completelyFreedUps.get()
        assert completelyFreedUp.parkingSpotId == parkingSpot.base.parkingSpotId
      }
  }
  
  def "reject to release empty parking spot"() {
    given:
      OccupiedParkingSpot parkingSpot = OccupiedParkingSpotFactory.create(emptyParkingSpotWith(4))
    
    when:
      Either<ReleasingFailed, VehicleLeftEvents> result = parkingSpot.releaseAll()
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "release all parked vehicle on occupied parking spot"() {
    given:
      OccupiedParkingSpot parkingSpot = OccupiedParkingSpotFactory.create(parkingSpotWith([vehicleWith(1), vehicleWith(1)]))
    
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
  
}
