package pl.cezarysanecki.parkingdomain.parking.model.model

import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotFixture.fullyOccupiedBy
import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotFixture.occupiedBy

class ReleasingOccupiedParkingSpotTest extends Specification {
  
  def "allow to release parking spot by vehicle"() {
    given:
      def vehicleToDriveAway = VehicleId.newOne()
    and:
      def occupiedParkingSpot = occupiedBy(VehicleId.newOne(), vehicleToDriveAway)
    
    when:
      def result = occupiedParkingSpot.release(vehicleToDriveAway)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == occupiedParkingSpot.parkingSpotId
        
        def parkingSpotLeft = it.released
        assert parkingSpotLeft.parkingSpotId == occupiedParkingSpot.parkingSpotId
        assert parkingSpotLeft.vehicleId == vehicleToDriveAway
      }
  }
  
  def "allow to release parking spot by vehicle to fully release it"() {
    given:
      def vehicleToDriveAway = VehicleId.newOne()
    and:
      def occupiedParkingSpot = fullyOccupiedBy(vehicleToDriveAway)
    
    when:
      def result = occupiedParkingSpot.release(vehicleToDriveAway)
    
    then:
      result.isRight()
      result.get().with {
        assert it.parkingSpotId == occupiedParkingSpot.parkingSpotId
        
        def parkingSpotLeft = it.released
        assert parkingSpotLeft.parkingSpotId == occupiedParkingSpot.parkingSpotId
        assert parkingSpotLeft.vehicleId == vehicleToDriveAway
        
        def completelyFreedUp = it.completelyFreedUp.get()
        assert completelyFreedUp.parkingSpotId == occupiedParkingSpot.parkingSpotId
      }
  }
  
  def "reject to release parking spot by vehicle when it is not parked on it"() {
    given:
      def vehicleToDriveAway = VehicleId.newOne()
    and:
      def occupiedParkingSpot = fullyOccupiedBy(VehicleId.newOne())
    
    when:
      def result = occupiedParkingSpot.release(vehicleToDriveAway)
    
    then:
      result.isLeft()
      result.getLeft().with {
        assert it.parkingSpotId == occupiedParkingSpot.parkingSpotId
        assert it.vehicleId == vehicleToDriveAway
        assert it.reason == "vehicle is not parked there"
      }
  }
  
}
