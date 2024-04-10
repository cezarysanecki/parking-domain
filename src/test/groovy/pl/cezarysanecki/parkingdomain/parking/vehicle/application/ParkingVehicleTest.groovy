package pl.cezarysanecki.parkingdomain.parking.vehicle.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle.Command
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class ParkingVehicleTest extends Specification {
  
  Vehicles vehicles = Mock()
  
  @Subject
  ParkingVehicle parkingVehicle = new ParkingVehicle(vehicles)
  
  def "allow to park vehicle on parking spot"() {
    given:
      def vehicle = notParkedVehicle()
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    
    when:
      def result = parkingVehicle.park(new Command(vehicle.vehicleInformation.vehicleId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject parking vehicle on parking spot when it is already parked"() {
    given:
      def vehicle = parkedVehicleOn(ParkingSpotId.newOne())
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    
    when:
      def result = parkingVehicle.park(new Command(vehicle.vehicleInformation.vehicleId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def "fail to park vehicle on parking spot when vehicle is not registered"() {
    given:
      def vehicleId = VehicleId.newOne()
    and:
      vehicles.findBy(vehicleId) >> Option.none()
    
    when:
      def result = parkingVehicle.park(new Command(vehicleId, ParkingSpotId.newOne()))
    
    then:
      result.isFailure()
  }
  
}