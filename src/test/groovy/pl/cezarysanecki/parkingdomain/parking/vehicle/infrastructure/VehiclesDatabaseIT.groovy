package pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.application.ParkingSpotReservationsFinder
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle.VehicleRegistered
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParked

@ActiveProfiles("local")
@SpringBootTest(classes = [VehicleConfig.class])
class VehiclesDatabaseIT extends Specification {
  
  def vehicleId = VehicleId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  @MockBean
  ParkingSpotReservationsFinder parkingSpotReservationsFinder
  
  @Autowired
  Vehicles vehicles
  
  def "persistence of vehicle in real database should work"() {
    when:
      vehicles.publish(vehicleRegistered(vehicleId))
    then:
      vehicleShouldBeFoundInDatabaseThatIsNotParked(vehicleId)
    
    when:
      vehicles.publish(vehicleParked(vehicleId))
    then:
      vehicleShouldBeFoundInDatabaseThatIsParked(vehicleId)
  }
  
  private VehicleRegistered vehicleRegistered(VehicleId vehicleId) {
    return new VehicleRegistered(vehicleId, VehicleSize.of(2))
  }
  
  private VehicleParked vehicleParked(VehicleId vehicleId) {
    return new VehicleParked(vehicleId, VehicleSize.of(2), ParkingSpotId.newOne())
  }
  
  private void vehicleShouldBeFoundInDatabaseThatIsNotParked(VehicleId vehicleId) {
    def vehicle = loadPersistedVehicle(vehicleId)
    assert vehicle.getParkedOn().isEmpty()
  }
  
  private void vehicleShouldBeFoundInDatabaseThatIsParked(VehicleId vehicleId) {
    def vehicle = loadPersistedVehicle(vehicleId)
    assert vehicle.getParkedOn().isDefined()
  }
  
  Vehicle loadPersistedVehicle(VehicleId vehicleId) {
    Option<Vehicle> loaded = vehicles.findBy(vehicleId)
    Vehicle vehicle = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return vehicle
  }
  
}
