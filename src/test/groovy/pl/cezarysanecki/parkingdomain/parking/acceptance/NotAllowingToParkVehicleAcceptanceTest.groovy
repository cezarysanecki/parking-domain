package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews

class NotAllowingToParkVehicleAcceptanceTest extends AbstractParkingAcceptanceTest {
  
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
      parkingVehicle.park(new ParkingVehicle.ParkOnChosenCommand(firstVehicleId, parkingSpotId))
    and:
      def secondVehicleId = registerVehicle(3)
    
    when:
      parkingVehicle.park(new ParkingVehicle.ParkOnChosenCommand(secondVehicleId, parkingSpotId))
    
    then:
      thisVehicleIsParked(firstVehicleId)
    and:
      thisVehicleIsNotParked(secondVehicleId)
  }
  
  void thisVehicleIsParked(VehicleId vehicleId) {
    assert vehicleViews.queryForParkedVehicles().any { it.vehicleId == vehicleId.value }
  }
  
  void thisVehicleIsNotParked(VehicleId vehicleId) {
    assert vehicleViews.queryForNotParkedVehicles().any { it.vehicleId == vehicleId.value }
  }
  
}
