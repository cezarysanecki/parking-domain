package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherConfig
import pl.cezarysanecki.parkingdomain.parking.ParkingConfig
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotViews
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingConfig.class, EventPublisherConfig.class])
class AllowingToParkVehicleAcceptanceTest extends Specification {
  
  @Autowired
  CreatingParkingSpot creatingParkingSpot
  @Autowired
  RegisteringVehicle registeringVehicle
  @Autowired
  ParkingVehicle parkingVehicle
  
  @Autowired
  VehicleViews vehicleViews
  @Autowired
  ParkingSpotViews parkingSpotViews
  
  def "allow to park on parking spot if there is enough space"() {
    given:
      def parkingSpotId = createParkingSpot(4)
    and:
      def firstVehicleId = registerVehicle(1)
    and:
      def secondVehicleId = registerVehicle(1)
    and:
      def thirdVehicleId = registerVehicle(2)
    
    when:
      parkingVehicle.park(new ParkingVehicle.Command(firstVehicleId, parkingSpotId))
    and:
      parkingVehicle.park(new ParkingVehicle.Command(secondVehicleId, parkingSpotId))
    and:
      parkingVehicle.park(new ParkingVehicle.Command(thirdVehicleId, parkingSpotId))
    
    then:
      thisVehiclesAreParkedOn(parkingSpotId, [firstVehicleId, secondVehicleId, thirdVehicleId])
    and:
      parkingSpotIsNotAvailable(parkingSpotId)
  }
  
  ParkingSpotId createParkingSpot(int capacity) {
    def result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
        ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold))
    return (result.get() as Result.Success<ParkingSpotId>).getResult()
  }
  
  VehicleId registerVehicle(int size) {
    def result = registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(size)))
    return (result.get() as Result.Success<VehicleId>).getResult()
  }
  
  void thisVehiclesAreParkedOn(ParkingSpotId parkingSpotId, List<VehicleId> vehicles) {
    def parkedVehicles = vehicleViews.queryForParkedVehicles()
        .stream()
        .filter { ParkingSpotId.of(it.parkingSpotId) == parkingSpotId }
        .toList()
    assert parkedVehicles.size() == vehicles.size()
    assert parkedVehicles.every { vehicles::contains }
  }
  
  void parkingSpotIsNotAvailable(ParkingSpotId parkingSpotId) {
    assert parkingSpotViews.queryForAvailableParkingSpots()
        .every { it.parkingSpotId != parkingSpotId.value }
  }
  
}
