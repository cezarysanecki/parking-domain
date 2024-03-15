package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.outOfOrderParkingSpot
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.parkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.reservedParkingSpotFor
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId

class ParkingVehicleTest extends Specification {
  
  ClientId clientId = anyClientId()
  
  def "vehicle can park if there is enough space"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(4)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
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
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
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
  
  def "vehicle can park on its reservation"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = reservedParkingSpotFor(clientId)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleParked.vehicle == vehicle
      }
  }
  
  def "cannot park on parking spot by too big vehicle"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(2)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("not enough space on parking spot")
      }
  }
  
  def "cannot park on parking spot by the same vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(2)
    and:
      ParkingSpot parkingSpot = parkingSpotWith(vehicle)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("vehicle is already parked on parking spot")
      }
  }
  
  def "cannot park on not own reserved parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = reservedParkingSpotFor(anyClientId())
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("parking spot is not reserved for this client")
      }
  }
  
  def "cannot park on out of order parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = outOfOrderParkingSpot()
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(clientId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("parking on out of order parking spot is forbidden")
      }
  }
  
}
