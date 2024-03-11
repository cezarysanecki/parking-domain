package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.model.*
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.*

class ReleasingParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  VehicleId vehicleId = anyVehicleId()
  
  ParkingSpots repository = Stub()
  
  def 'should successfully release parking spot if vehicle is parked on it'() {
    given:
      ReleasingParkingSpot releasingParkingSpot = new ReleasingParkingSpot(repository)
    and:
      persisted(parkingSpotWith(parkingSpotId, vehicleWith(vehicleId)))
    
    when:
      def result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(parkingSpotId, vehicleId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject releasing parking spot if specified vehicle is not parked on it'() {
    given:
      ReleasingParkingSpot releasingParkingSpot = new ReleasingParkingSpot(repository)
    and:
      persisted(emptyParkingSpotWith(parkingSpotId, 1))
    
    when:
      def result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(parkingSpotId, vehicleId))
    
    then:
      result.isSuccess()
      result.get() == Result.Rejection
  }
  
  def 'should fail if parking spot does not exist'() {
    given:
      ReleasingParkingSpot releasingParkingSpot = new ReleasingParkingSpot(repository)
    and:
      unknownParkingSpot()
    
    when:
      def result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(parkingSpotId, vehicleId))
    
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
