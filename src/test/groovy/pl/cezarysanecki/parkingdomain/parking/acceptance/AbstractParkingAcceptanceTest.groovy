package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.ParkingConfig
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.FindingParkingSpotReservations
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ParkingConfig.class,
    EventPublisherTestConfig.class])
abstract class AbstractParkingAcceptanceTest extends Specification {
  
  @MockBean
  FindingParkingSpotReservations parkingSpotReservationsFinder
  
  @Autowired
  RegisteringVehicle registeringVehicle
  @Autowired
  CreatingParkingSpot creatingParkingSpot
  
  ParkingSpotId createParkingSpot(int capacity) {
    def result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
        ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold))
    return (result.get() as Result.Success<ParkingSpotId>).getResult()
  }
  
  VehicleId registerVehicle(int size) {
    def result = registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(size)))
    return (result.get() as Result.Success<VehicleId>).getResult()
  }
  
}
