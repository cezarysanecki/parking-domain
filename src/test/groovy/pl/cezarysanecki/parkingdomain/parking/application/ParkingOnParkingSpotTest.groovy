package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.OpenParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId

class ParkingOnParkingSpotTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  Vehicle alrightVehicle = vehicleWith(1)
  Vehicle tooBigVehicle = vehicleWith(2)
  
  ParkingSpots repository = Stub()
  
  def 'should successfully parking vehicle if parking spot has enough place'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      persisted(emptyParkingSpotWith(parkingSpotId, 1))
    
    when:
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(clientId, parkingSpotId, alrightVehicle))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject parking vehicle if it is too big for parking spot'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      persisted(emptyParkingSpotWith(parkingSpotId, 1))
    
    when:
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(clientId, parkingSpotId, tooBigVehicle))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if parking spot does not exist'() {
    given:
      ParkingOnParkingSpot parkingOnParkingSpot = new ParkingOnParkingSpot(repository)
    and:
      unknownParkingSpot()
    
    when:
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(clientId, parkingSpotId, alrightVehicle))
    
    then:
      result.isFailure()
  }
  
  OpenParkingSpot persisted(OpenParkingSpot parkingSpot) {
    repository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    repository.publish(_ as ParkingSpotEvent) >> parkingSpot
    return parkingSpot
  }
  
  ParkingSpotId unknownParkingSpot() {
    repository.findBy(parkingSpotId) >> Option.none()
    return parkingSpotId
  }
  
}
