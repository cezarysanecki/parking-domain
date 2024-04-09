package pl.cezarysanecki.parkingdomain.parking.parkingspot.model

import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.CompletelyFreedUp
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotLeft

class ReleasingOccupiedParkingSpotTest extends Specification {
  
  def "allow to release parking spot by vehicle"() {
    given:
      def vehicleToDriveAway = VehicleId.newOne()
    and:
      def occupiedParkingSpot = ParkingSpotFixture.occupiedBy(
          VehicleId.newOne(), vehicleToDriveAway)
    
    when:
      def result = occupiedParkingSpot.release(vehicleToDriveAway)
    
    then:
      result.isRight()
      result.get().with {
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
        
        ParkingSpotLeft parkingSpotLeft = it.parkingSpotLeft
        parkingSpotLeft.parkingSpotId == occupiedParkingSpot.parkingSpotId
        parkingSpotLeft.vehicleId == vehicleToDriveAway
      }
  }
  
  def "allow to release parking spot by vehicle to fully release it"() {
    given:
      def vehicleToDriveAway = VehicleId.newOne()
    and:
      def occupiedParkingSpot = ParkingSpotFixture.fullyOccupiedBy(vehicleToDriveAway)
    
    when:
      def result = occupiedParkingSpot.release(vehicleToDriveAway)
    
    then:
      result.isRight()
      result.get().with {
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
        
        ParkingSpotLeft parkingSpotLeft = it.parkingSpotLeft
        parkingSpotLeft.parkingSpotId == occupiedParkingSpot.parkingSpotId
        parkingSpotLeft.vehicleId == vehicleToDriveAway
        
        CompletelyFreedUp completelyFreedUp = it.completelyFreedUp.get()
        completelyFreedUp.parkingSpotId == occupiedParkingSpot.parkingSpotId
      }
  }
  
  def "reject to release parking spot by vehicle when it is not parked on it"() {
    given:
      def vehicleToDriveAway = VehicleId.newOne()
    and:
      def occupiedParkingSpot = ParkingSpotFixture.fullyOccupiedBy(VehicleId.newOne())
    
    when:
      def result = occupiedParkingSpot.release(vehicleToDriveAway)
    
    then:
      result.isLeft()
      result.getLeft().with {
        it.parkingSpotId == occupiedParkingSpot.parkingSpotId
        it.vehicleId == vehicleToDriveAway
        it.reason == "vehicle is not parked there"
      }
  }
  
}
