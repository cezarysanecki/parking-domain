package pl.cezarysanecki.parkingdomain.parking.model

import io.vavr.control.Either
import spock.lang.Specification

import java.time.Instant

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFailed
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.emptyParkingSpotWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

class ReservingParkingSpotTest extends Specification {
  
  def "can reserve parking spot for vehicle"() {
    given:
      Vehicle vehicle = vehicleWith(1)
    and:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    
    when:
      Either<ReservationFailed, ReservationMade> result = parkingSpot.reserve(Set.of(vehicle), Instant.now())
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicles == Set.of(vehicle.vehicleId)
      }
  }
  
  def "cannot make reservation where vehicles size is too big for parking spot"() {
    given:
      ParkingSpot parkingSpot = emptyParkingSpotWith(1)
    and:
      Vehicle vehicle = vehicleWith(2)
    
    when:
      Either<ReservationFailed, ReservationMade> result = parkingSpot.reserve(Set.of(vehicle), Instant.now())
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == parkingSpot.parkingSpotId
        assert it.vehicles == Set.of(vehicle.vehicleId)
      }
  }
  
}
