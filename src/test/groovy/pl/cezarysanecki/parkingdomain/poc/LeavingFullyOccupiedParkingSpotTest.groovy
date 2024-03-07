package pl.cezarysanecki.parkingdomain.poc

import spock.lang.Specification

class LeavingFullyOccupiedParkingSpotTest extends Specification {
  
  def "free fully occupied parking spot after being left by last vehicle"() {
    given:
      def vehicle = VehicleId.of(1)
    and:
      def fullyOccupiedParkingSpot = new FullyOccupiedParkingSpot(ParkingSpotId.of(1), 1, [vehicle].toSet())
    
    when:
      def result = fullyOccupiedParkingSpot.leaveBy vehicle
    
    then:
      result in FreeParkingSpot
  }
  
  def "fully occupied parking spot should be partially occupied after being left by one of vehicles"() {
    given:
      def vehicle = VehicleId.of(1)
    and:
      def fullyOccupiedParkingSpot = new FullyOccupiedParkingSpot(ParkingSpotId.of(1), 4, [vehicle, VehicleId.of(2)].toSet())
    
    when:
      def result = fullyOccupiedParkingSpot.leaveBy vehicle
    
    then:
      result in PartiallyOccupiedParkingSpot
  }
  
}
