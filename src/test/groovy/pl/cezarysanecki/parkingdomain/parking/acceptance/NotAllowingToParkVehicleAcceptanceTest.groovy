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
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingConfig.class, EventPublisherConfig.class])
class NotAllowingToParkVehicleAcceptanceTest extends Specification {
  
  @Autowired
  CreatingParkingSpot creatingParkingSpot
  @Autowired
  RegisteringVehicle registeringVehicle
  @Autowired
  ParkingVehicle parkingVehicle
  @Autowired
  VehicleViews vehicleViews
  
  def "cannot park on parking spot if vehicle is too big for left space"() {
    given:
      def parkingSpotId = createParkingSpot(4)
    and:
      def firstVehicleId = registerVehicle(2)
    and:
      parkingVehicle.park(new ParkingVehicle.Command(firstVehicleId, parkingSpotId))
    and:
      def secondVehicleId = registerVehicle(3)
    
    when:
      parkingVehicle.park(new ParkingVehicle.Command(secondVehicleId, parkingSpotId))
    
    then:
      thisVehicleIsParked(firstVehicleId)
    and:
      thisVehicleIsNotParked(secondVehicleId)
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
  
  void thisVehicleIsParked(VehicleId vehicleId) {
    assert vehicleViews.queryForParkedVehicles().any { it.vehicleId == vehicleId.value }
  }
  
  void thisVehicleIsNotParked(VehicleId vehicleId) {
    assert vehicleViews.queryForNotParkedVehicles().any { it.vehicleId == vehicleId.value }
  }
  
}
