package pl.cezarysanecki.parkingdomain.parking.vehicle.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.FindingParkingSpotReservations
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle.ParkOnReservedCommand
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle

class ParkingVehicleUsingReservationTest extends Specification {
  
  Vehicles vehicles = Mock()
  FindingParkingSpotReservations parkingSpotReservationsFinder = Mock()
  
  ReservationId reservationId = ReservationId.newOne()
  
  @Subject
  ParkingVehicle parkingVehicle = new ParkingVehicle(vehicles, parkingSpotReservationsFinder)
  
  def "allow to park vehicle on parking spot using reservation"() {
    given:
      def vehicle = notParkedVehicle()
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    and:
      parkingSpotReservationsFinder.findParkingSpotIdByAssigned(reservationId) >> Option.of(ParkingSpotId.newOne())
    
    when:
      def result = parkingVehicle.park(new ParkOnReservedCommand(vehicle.vehicleInformation.vehicleId, reservationId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "fail to park vehicle on parking spot when there is no parking spot linked with reservation"() {
    given:
      def vehicle = notParkedVehicle()
    and:
      vehicles.findBy(vehicle.vehicleInformation.vehicleId) >> Option.of(vehicle)
    and:
      parkingSpotReservationsFinder.findParkingSpotIdByAssigned(reservationId) >> Option.none()
    
    when:
      def result = parkingVehicle.park(new ParkOnReservedCommand(vehicle.vehicleInformation.vehicleId, reservationId))
    
    then:
      result.isFailure()
  }
  
}
