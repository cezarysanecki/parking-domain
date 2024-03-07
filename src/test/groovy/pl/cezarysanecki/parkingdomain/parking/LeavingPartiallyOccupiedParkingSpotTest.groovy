package pl.cezarysanecki.parkingdomain.parking

import pl.cezarysanecki.parkingdomain.parking.model.FreeParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.PartiallyOccupiedParkingSpot
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId
import spock.lang.Specification

class LeavingPartiallyOccupiedParkingSpotTest extends Specification {
  
  def "free partially occupied parking spot after being left by last vehicle"() {
    given:
      def vehicle = VehicleId.of(1)
    and:
      def partiallyOccupiedParkingSpot = new PartiallyOccupiedParkingSpot(ParkingSpotId.of(1), 2, [vehicle].toSet())
    
    when:
      def result = partiallyOccupiedParkingSpot.leaveBy vehicle
    
    then:
      result in FreeParkingSpot
  }
  
  def "still be partially occupied parking spot after being left by one of vehicles"() {
    given:
      def vehicle = VehicleId.of(1)
    and:
      def partiallyOccupiedParkingSpot = new PartiallyOccupiedParkingSpot(ParkingSpotId.of(1), 2, [vehicle, VehicleId.of(2)].toSet())
    
    when:
      def result = partiallyOccupiedParkingSpot.leaveBy vehicle
    
    then:
      result in PartiallyOccupiedParkingSpot
  }
  
}
