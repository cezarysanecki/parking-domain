package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class ReservingParkingSpotTest extends Specification {
  
  def "can reserve parking spot for vehicle when there is space"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(4)
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingSpotEvent.ReservationFailed, ParkingSpotEvent.ReservationMade> result = parkingSpot.reserveFor(vehicle)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
      }
  }
  
  def "cannot reserve parking spot for vehicle when there is space"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      parkingSpot.reserveFor(vehicleWith(1))
    and:
      Vehicle vehicle = vehicleWith(1)
    
    when:
      Either<ParkingSpotEvent.ReservationFailed, ParkingSpotEvent.ReservationMade> result = parkingSpot.reserveFor(vehicle)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicleId == vehicle.vehicleId
      }
  }
  
}
