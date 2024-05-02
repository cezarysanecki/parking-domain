package pl.cezarysanecki.parkingdomain.parking.vehicle.application

import pl.cezarysanecki.parkingdomain.management.vehicle.RegisteringVehicle
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleRegistered
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import spock.lang.Specification
import spock.lang.Subject

class RegisteringVehicleTest extends Specification {
  
  Vehicles vehicles = Mock()
  
  @Subject
  RegisteringVehicle registeringVehicle = new RegisteringVehicle(vehicles)
  
  def "allow to register vehicle"() {
    given:
      def vehicleSize = SpotUnits.of(2)
    
    when:
      def result = registeringVehicle.register(new Command(vehicleSize))
    
    then:
      result.isSuccess()
    and:
      1 * vehicles.publish({
        it.vehicleSize == vehicleSize
      } as VehicleRegistered)
  }
  
  def "fail to register vehicle when publishing fails"() {
    given:
      vehicles.publish(_ as VehicleRegistered) >> {
        throw new IllegalStateException()
      }
    
    when:
      def result = registeringVehicle.register(new Command(SpotUnits.of(2)))
    
    then:
      result.isFailure()
  }
  
}
