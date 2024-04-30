package pl.cezarysanecki.parkingdomain.requesting.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.ParkingConfig
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.management.vehicle.RegisteringVehicle
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleSize
import pl.cezarysanecki.parkingdomain.requesting.RequestingConfig
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
    def result = registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(size)))
    return (result.get() as Result.Success<VehicleId>).getResult()
  }
  
  
}
