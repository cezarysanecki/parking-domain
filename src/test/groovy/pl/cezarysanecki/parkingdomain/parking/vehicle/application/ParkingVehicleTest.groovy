package pl.cezarysanecki.parkingdomain.parking.vehicle.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.ParkingSpotReservationsFinder
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle.ParkOnChosenCommand
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class ParkingVehicleTest extends Specification {
  
  Vehicles vehicles = Mock()
  ParkingSpotReservationsFinder parkingSpotReservationsFinder = Mock()
  
  @Subject
  ParkingVehicle parkingVehicle = new ParkingVehicle(vehicles, parkingSpotReservationsFinder)
  
  def "allow to park vehicle on parking spot"() {
    given:
      def vehicle = notParkedVehicle()
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    
    when:
      def result = parkingVehicle.park(new ParkOnChosenCommand(vehicle.vehicleInformation.vehicleId, ParkingSpotId.newOne()))
    
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
      def result = parkingVehicle.park(new ParkOnChosenCommand(vehicle.vehicleInformation.vehicleId, ParkingSpotId.newOne()))
    
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
      def result = parkingVehicle.park(new ParkOnChosenCommand(vehicleId, ParkingSpotId.newOne()))
    
    then:
      result.isFailure()
  }
  
}
