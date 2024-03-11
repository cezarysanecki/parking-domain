package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.*

class ReleasingParkingSpotTest extends Specification {
  
  def "can release parked vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      NormalParkingSpot parkingSpot = parkingSpotWith(vehicle)
    
    when:
      Either<ReleasingFailed, VehicleLeft> result = parkingSpot.release(vehicle.vehicleId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicle == vehicle
      }
  }
  
  def "vehicle cannot be release from parking spot if it is not on this spot"() {
    given:
      NormalParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ReleasingFailed, VehicleLeft> result = parkingSpot.release(vehicle.vehicleId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
      }
  }
  
}
