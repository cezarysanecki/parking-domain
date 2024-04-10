package pl.cezarysanecki.parkingdomain.parking.vehicle.model


import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class ParkingVehicleOnParkingSpotTest extends Specification {
  
  def "allow to park vehicle on parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def vehicle = notParkedVehicle()
    
    when:
      def result = vehicle.parkOn(parkingSpotId)
    
    then:
      result.isRight()
      result.get().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
        assert it.vehicleSize == vehicle.vehicleInformation.vehicleSize
        assert it.parkingSpotId == parkingSpotId
      }
  }
  
  def "fail to park vehicle on parking spot when it is already parked"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def vehicle = parkedVehicleOn(ParkingSpotId.newOne())
    
    when:
      def result = vehicle.parkOn(parkingSpotId)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
        assert it.reason == "vehicle is already parked"
      }
  }
  
}
