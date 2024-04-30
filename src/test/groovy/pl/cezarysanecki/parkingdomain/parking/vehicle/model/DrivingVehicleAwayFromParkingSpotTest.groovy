package pl.cezarysanecki.parkingdomain.parking.vehicle.model


import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.notParkedVehicle
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleFixture.parkedVehicleOn

class DrivingVehicleAwayFromParkingSpotTest extends Specification {
  
  def "allow to drive vehicle away from parking spot"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      def vehicle = parkedVehicleOn(parkingSpotId)
    
    when:
      def result = vehicle.driveAway()
    
    then:
      result.isRight()
      result.get().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
      }
  }
  
  def "fail to drive vehicle away from parking spot when it is not parked"() {
    given:
      def vehicle = notParkedVehicle()
    
    when:
      def result = vehicle.driveAway()
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.vehicleId == vehicle.vehicleInformation.vehicleId
        assert it.reason == "vehicle is not parked"
      }
  }
  
}
