package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.application.parking.ParkReservedVehicleCommand
import pl.cezarysanecki.parkingdomain.parking.application.parking.ParkVehicleCommand
import pl.cezarysanecki.parkingdomain.parking.application.parking.ParkingOnParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType.Gold
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsFixture.anyReservationId

class ParkingOnParkingSpotTest extends Specification {
  
  ReservationId reservationId = anyReservationId()
  ParkingSpotType parkingSpotType = Gold
  
  Vehicle alrightVehicle = vehicleWith(1)
  Vehicle tooBigVehicle = vehicleWith(2)
  
  ParkingSpots repository = Stub()
  
  def 'should successfully parking vehicle if open parking spot has enough place'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      persistedOpen(alrightVehicle, emptyParkingSpotWith(1))
    
    when:
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(parkingSpotType, alrightVehicle))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject parking vehicle if it is too big for open parking spot'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      persistedOpen(tooBigVehicle, emptyParkingSpotWith(1))
    
    when:
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(parkingSpotType, tooBigVehicle))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if open parking spot does not exist'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      unknownParkingSpot(alrightVehicle)
    
    when:
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(parkingSpotType, alrightVehicle))
    
    then:
      result.isFailure()
  }
  
  def 'should successfully parking vehicle if reserved parking spot has enough place'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      persistedReserved(emptyParkingSpotWith(1))
    
    when:
      def result = parkingOnParkingSpot.park(new ParkReservedVehicleCommand(reservationId, alrightVehicle))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject parking vehicle if it is too big for reserved parking spot'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      persistedReserved(emptyParkingSpotWith(1))
    
    when:
      def result = parkingOnParkingSpot.park(new ParkReservedVehicleCommand(reservationId, tooBigVehicle))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if reserved parking spot does not exist'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      unknownParkingSpot(reservationId)
    
    when:
      def result = parkingOnParkingSpot.park(new ParkReservedVehicleCommand(reservationId, alrightVehicle))
    
    then:
      result.isFailure()
  }
  
  ParkingSpot persistedOpen(Vehicle vehicle, ParkingSpot parkingSpot) {
    repository.findBy(parkingSpotType, vehicle.vehicleSizeUnit) >> Option.of(parkingSpot)
    repository.publish(_ as ParkingSpotEvent) >> parkingSpot
    return parkingSpot
  }
  
  ParkingSpot persistedReserved(ParkingSpot parkingSpot) {
    repository.findBy(reservationId) >> Option.of(parkingSpot)
    repository.publish(_ as ParkingSpotEvent) >> parkingSpot
    return parkingSpot
  }
  
  void unknownParkingSpot(Vehicle vehicle) {
    repository.findBy(parkingSpotType, vehicle.vehicleSizeUnit) >> Option.none()
  }
  
  void unknownParkingSpot(ReservationId reservationId) {
    repository.findBy(reservationId) >> Option.none()
  }
  
}
