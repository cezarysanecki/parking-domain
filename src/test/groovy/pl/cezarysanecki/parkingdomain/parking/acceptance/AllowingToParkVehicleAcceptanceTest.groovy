package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotViews
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews
import spock.lang.Ignore

@Ignore("#256")
class AllowingToParkVehicleAcceptanceTest extends AbstractParkingAcceptanceTest {
  
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
      parkingVehicle.park(new ParkingVehicle.ParkOnChosenCommand(firstVehicleId, parkingSpotId))
    and:
      parkingVehicle.park(new ParkingVehicle.ParkOnChosenCommand(secondVehicleId, parkingSpotId))
    and:
      parkingVehicle.park(new ParkingVehicle.ParkOnChosenCommand(thirdVehicleId, parkingSpotId))
    
    then:
      vehiclesAreParkedOn(parkingSpotId, [firstVehicleId, secondVehicleId, thirdVehicleId])
    and:
      parkingSpotIsNotAvailable(parkingSpotId)
  }
  
  void vehiclesAreParkedOn(ParkingSpotId parkingSpotId, List<VehicleId> vehicles) {
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
