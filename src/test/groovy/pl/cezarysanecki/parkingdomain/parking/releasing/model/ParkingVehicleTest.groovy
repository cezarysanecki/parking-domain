package pl.cezarysanecki.parkingdomain.parking.releasing.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.*
import static pl.cezarysanecki.parkingdomain.parking.releasing.model.ParkingSpotFixture.*

class ParkingVehicleTest extends Specification {
  
  def "vehicle can park if there is enough space"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(4)
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
      }
  }
  
  def "vehicle can park occupying fully parking spot"() {
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
  
  def "vehicle can park on its reservation"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = reservedParkingSpotFor(vehicle.vehicleId)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == parkingSpot.parkingSpotId
        assert vehicleParked.vehicle == vehicle
        
        ReservationFulfilled reservationFulfilled = it.reservationFulfilled.get()
        assert reservationFulfilled.parkingSpotId == parkingSpot.parkingSpotId
        assert reservationFulfilled.vehicleId == vehicle.vehicleId
      }
  }
  
  def "vehicle cannot park is too big for parking spot"() {
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
      }
  }
  
}
