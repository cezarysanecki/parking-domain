package pl.cezarysanecki.parkingdomain.parking.vehicle.application


import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle.Command
import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle.VehicleRegistered

class RegisteringVehicleTest extends Specification {
  
  Vehicles vehicles = Mock()
  
  @Subject
  RegisteringVehicle registeringVehicle = new RegisteringVehicle(vehicles)
  
  def "allow to register vehicle"() {
    given:
      def vehicleSize = VehicleSize.of(2)
    
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
      def result = registeringVehicle.register(new Command(VehicleSize.of(2)))
    
    then:
      result.isFailure()
  }
  
}
