package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.outOfOrderParkingSpot
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.parkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyReservationId

class ParkingOnReservedParkingSpotTest extends Specification {
  
  ReservationId reservationId = anyReservationId()
  
  def "cannot park on out of order reserved parking spot"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ReservedParkingSpot parkingSpot = ParkingSpotFactory.createReserved(outOfOrderParkingSpot(), reservationId)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(reservationId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.base.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("parking on out of order parking spot is forbidden")
      }
  }
  
  def "cannot park on reserved parking spot by the same vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(2)
    and:
      ReservedParkingSpot parkingSpot = ParkingSpotFactory.createReserved(parkingSpotWith(vehicle), reservationId)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(reservationId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.base.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("vehicle is already parked on parking spot")
      }
  }
  
  def "cannot park on reserved parking spot by too big vehicle"() {
    given:
      ReservedParkingSpot parkingSpot = ParkingSpotFactory.createReserved(emptyParkingSpotWith(1), reservationId)
    and:
      Vehicle vehicle = vehicleWith(2)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(reservationId, vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.base.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("not enough space on parking spot")
      }
  }
  
  def "cannot park on reserved parking spot without proper reservation"() {
    given:
      ReservedParkingSpot parkingSpot = ParkingSpotFactory.createReserved(emptyParkingSpotWith(1), reservationId)
    and:
      Vehicle vehicle = vehicleWith(2)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(anyReservationId(), vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.base.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
        assert it.reason.contains("parking spot is not reserved for this client")
      }
  }
  
  def "vehicle can park on reserved parking spot if there is enough space fulfilling reservation"() {
    given:
      ReservedParkingSpot parkingSpot = ParkingSpotFactory.createReserved(emptyParkingSpotWith(4), reservationId)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(reservationId, vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == parkingSpot.base.parkingSpotId
        assert vehicleParked.vehicle == vehicle
        
        ReservationFulfilled reservationFulfilled = it.reservationFulfilled.get()
        assert reservationFulfilled.parkingSpotId == parkingSpot.base.parkingSpotId
        assert reservationFulfilled.reservationId == reservationId
      }
  }
  
  def "vehicle can park fully occupying reserved parking spot fulfilling reservation"() {
    given:
      ReservedParkingSpot parkingSpot = ParkingSpotFactory.createReserved(emptyParkingSpotWith(1), reservationId)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingFailed, VehicleParkedEvents> result = parkingSpot.park(reservationId, vehicle)
    
    then:
      result.isRight()
      result.get().with {
        VehicleParked vehicleParked = it.vehicleParked
        assert vehicleParked.parkingSpotId == parkingSpot.base.parkingSpotId
        assert vehicleParked.vehicle == vehicle
        
        ReservationFulfilled reservationFulfilled = it.reservationFulfilled.get()
        assert reservationFulfilled.parkingSpotId == parkingSpot.base.parkingSpotId
        assert reservationFulfilled.reservationId == reservationId
        
        FullyOccupied fullyOccupied = it.fullyOccupied.get()
        assert fullyOccupied.parkingSpotId == parkingSpot.base.parkingSpotId
      }
  }
  
}
