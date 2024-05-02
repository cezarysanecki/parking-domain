package pl.cezarysanecki.parkingdomain.parking.vehicle.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupationFailed
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class DrivingVehicleAwayAfterFailedParkingSpotOccupationTest extends Specification {
  
  Vehicles vehicles = Mock()
  
  @Subject
  ParkingSpotEventsHandler parkingSpotEventsHandler = new ParkingSpotEventsHandler(
      new DrivingVehicleAway(vehicles))
  
  def "drive vehicle away when parking spot occupation fails"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def vehicle = parkedVehicleOn(parkingSpotId)
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    
    when:
      parkingSpotEventsHandler.handle(new OccupationFailed(
          parkingSpotId, vehicle.vehicleInformation.vehicleId, "any reason"))
    
    then:
      1 * vehicles.publish({
        it.vehicleId == vehicle.vehicleInformation.vehicleId
      } as VehicleDroveAway)
  }
  
}
