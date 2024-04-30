package pl.cezarysanecki.parkingdomain.parking.vehicle.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.DrivingVehicleAway.Command
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class DrivingVehicleAwayTest extends Specification {
  
  Vehicles vehicles = Mock()
  
  @Subject
  DrivingVehicleAway drivingVehicleAway = new DrivingVehicleAway(vehicles)
  
  def "allow to drive vehicle away from parking spot"() {
    given:
      def vehicle = parkedVehicleOn(ParkingSpotId.newOne())
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    
    when:
      def result = drivingVehicleAway.driveAway(new Command(vehicle.vehicleInformation.vehicleId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject driving vehicle away from parking spot when it is not parked"() {
    given:
      def vehicle = notParkedVehicle()
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    
    when:
      def result = drivingVehicleAway.driveAway(new Command(vehicle.vehicleInformation.vehicleId))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def "fail to drive vehicle away from parking spot when vehicle is not registered"() {
    given:
      def vehicleId = VehicleId.newOne()
    and:
      vehicles.findBy(vehicleId) >> Option.none()
    
    when:
      def result = drivingVehicleAway.driveAway(new Command(vehicleId))
    
    then:
      result.isFailure()
  }
  
}
