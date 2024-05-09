package pl.cezarysanecki.parkingdomain.acceptance.requestingreservation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingConfig
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.management.vehicle.RegisteringVehicle
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits

import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ParkingConfig.class,
    RequestingConfig.class,
    EventPublisherTestConfig.class])
abstract class AbstractRequestingAcceptanceTest extends Specification {
  
  @Autowired
  AddingParkingSpot creatingParkingSpot
  @Autowired
  RegisteringVehicle registeringVehicle
  
  ParkingSpotId createParkingSpot(int capacity) {
    final AddingParkingSpot.Command command = new AddingParkingSpot.Command(
        ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold)
    def result = creatingParkingSpot.addParkingSpot(command.parkingSpotCapacity, command.parkingSpotCategory)
    return (result.get() as Result.Success<ParkingSpotId>).getResult()
  }
  
  VehicleId registerVehicle(int size) {
    def result = registeringVehicle.register(new RegisteringVehicle.Command(SpotUnits.of(size)))
    return (result.get() as Result.Success<VehicleId>).getResult()
  }
  
  
}