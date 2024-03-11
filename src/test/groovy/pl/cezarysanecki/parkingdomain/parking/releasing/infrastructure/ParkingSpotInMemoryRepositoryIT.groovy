package pl.cezarysanecki.parkingdomain.parking.releasing.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.parking.infrastructure.ParkingSpotConfig
import pl.cezarysanecki.parkingdomain.parking.model.*
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.releasing.model.ParkingSpotFixture.*

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotConfig.class])
class ParkingSpotInMemoryRepositoryIT extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  VehicleId vehicleId = anyVehicleId()
  
  Vehicle vehicle = vehicleWith(vehicleId, 1)
  
  @Autowired
  ParkingSpots parkingSpots
  
  def 'persistence in database should work'() {
    when:
      parkingSpots.publish parkingSpotCreated()
    
    then:
      parkingSpotShouldBeEmpty(parkingSpotId)
    
    and:
      parkingSpots.publish vehicleParked()
    
    then:
      parkingSpotShouldContainParkedVehicle(parkingSpotId, vehicleId)
  }
  
  void parkingSpotShouldBeEmpty(ParkingSpotId parkingSpotId) {
    def parkingSpot = loadPersistedParkingSpot(parkingSpotId)
    assert parkingSpot.isEmpty()
  }
  
  void parkingSpotShouldContainParkedVehicle(ParkingSpotId parkingSpotId, VehicleId vehicleId) {
    def parkingSpot = loadPersistedParkingSpot(parkingSpotId)
    assert parkingSpot.isParked(vehicleId)
  }
  
  ParkingSpotCreated parkingSpotCreated() {
    return new ParkingSpotCreated(parkingSpotId)
  }
  
  VehicleParked vehicleParked() {
    return new VehicleParked(parkingSpotId, vehicle)
  }
  
  ParkingSpot loadPersistedParkingSpot(ParkingSpotId parkingSpotId) {
    Option<ParkingSpot> loaded = parkingSpots.findBy(parkingSpotId)
    ParkingSpot parkingSpot = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpot
  }
}
