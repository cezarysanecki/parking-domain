package pl.cezarysanecki.parkingdomain.reservationschedule.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.infrastructure.ParkingSpotConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyVehicleId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class FreeParkingSpotIT extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  @Autowired
  ParkingSpots parkingSpots
  
  def 'persistence in database should work'() {
    given:
      parkingSpots.publish parkingSpotCreated(4)
    
    when:
      Vehicle firstVehicle = vehicleWith(anyVehicleId(), 2)
      parkingSpots.publish vehicleParked(firstVehicle)
    and:
      Vehicle secondVehicle = vehicleWith(anyVehicleId(), 1)
      parkingSpots.publish vehicleParked(secondVehicle)
    and:
      Vehicle thirdVehicle = vehicleWith(anyVehicleId(), 1)
      parkingSpots.publish vehicleParked(thirdVehicle)
    
    then:
      parkingSpotShouldBeFull(parkingSpotId)
    
    when:
      parkingSpots.publish vehicleLeft(firstVehicle)
    and:
      parkingSpots.publish vehicleLeft(secondVehicle)
    and:
      parkingSpots.publish vehicleLeft(thirdVehicle)
    
    then:
      parkingSpotShouldBeEmpty(parkingSpotId)
  }
  
  void parkingSpotShouldBeFull(ParkingSpotId parkingSpotId) {
    def parkingSpot = loadPersistedParkingSpot(parkingSpotId)
    assert parkingSpot.isFull()
  }
  
  void parkingSpotShouldBeEmpty(ParkingSpotId parkingSpotId) {
    def parkingSpot = loadPersistedParkingSpot(parkingSpotId)
    assert parkingSpot.isEmpty()
  }
  
  ParkingSpotCreated parkingSpotCreated(int capacity) {
    return new ParkingSpotCreated(parkingSpotId, capacity)
  }
  
  VehicleParked vehicleParked(Vehicle vehicle) {
    return new VehicleParked(parkingSpotId, vehicle)
  }
  
  VehicleLeft vehicleLeft(Vehicle vehicle) {
    return new VehicleLeft(parkingSpotId, vehicle)
  }
  
  ParkingSpot loadPersistedParkingSpot(ParkingSpotId parkingSpotId) {
    Option<ParkingSpot> loaded = parkingSpots.findBy(parkingSpotId)
    ParkingSpot parkingSpot = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpot
  }
}
