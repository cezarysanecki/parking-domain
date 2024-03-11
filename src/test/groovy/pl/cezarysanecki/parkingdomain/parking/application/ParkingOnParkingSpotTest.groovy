package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.*
import spock.lang.Specification

import java.time.Instant

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.*

class ParkingOnParkingSpotTest extends Specification {
  
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
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(parkingSpotId, alrightVehicle, Instant.now()))
    
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
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(parkingSpotId, tooBigVehicle, Instant.now()))
    
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
      def result = parkingOnParkingSpot.park(new ParkVehicleCommand(parkingSpotId, alrightVehicle, Instant.now()))
    
    then:
      result.isFailure()
  }
  
  ParkingSpot persisted(ParkingSpot parkingSpot) {
    repository.findBy(parkingSpot.parkingSpotId) >> Option.of(parkingSpot)
    repository.publish(_ as ParkingSpotEvent) >> parkingSpot
    return parkingSpot
  }
  
  ParkingSpotId unknownParkingSpot() {
    repository.findBy(parkingSpotId) >> Option.none()
    return parkingSpotId
  }
  
}
