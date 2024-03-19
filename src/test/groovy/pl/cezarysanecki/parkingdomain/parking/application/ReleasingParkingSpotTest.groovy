package pl.cezarysanecki.parkingdomain.parking.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.application.releasing.ReleaseParkingSpotCommand
import pl.cezarysanecki.parkingdomain.parking.application.releasing.ReleasingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.model.releasing.OccupiedParkingSpotFactory
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyVehicleId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.parkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class ReleasingParkingSpotTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  VehicleId vehicleId = anyVehicleId()
  
  ParkingSpots repository = Stub()
  
  def 'should successfully release parking spot if vehicle is parked on it'() {
    given:
      ReleasingParkingSpot releasingParkingSpot = new ReleasingParkingSpot(repository)
    and:
      persisted(OccupiedParkingSpotFactory.create(parkingSpotWith(vehicleWith(vehicleId))))
    
    when:
      def result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(vehicleId))
    
    then:
      result.isSuccess()
      result.get() == Result.Success
  }
  
  def 'should reject releasing parking spot if specified vehicle is not parked on it'() {
    given:
      ReleasingParkingSpot releasingParkingSpot = new ReleasingParkingSpot(repository)
    and:
      persisted(OccupiedParkingSpotFactory.create(emptyParkingSpotWith(1)))
    
    when:
      def result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(vehicleId))
    
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
      def result = releasingParkingSpot.release(new ReleaseParkingSpotCommand(vehicleId))
    
    then:
      result.isFailure()
  }
  
  ParkingSpot persisted(ParkingSpot parkingSpot) {
    repository.findBy(vehicleId) >> Option.of(parkingSpot)
    repository.publish(_ as ParkingSpotEvent) >> parkingSpot
    return parkingSpot
  }
  
  ParkingSpotId unknownParkingSpot() {
    repository.findBy(vehicleId) >> Option.none()
    return parkingSpotId
  }
  
}
