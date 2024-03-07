package pl.cezarysanecki.parkingdomain.poc

import spock.lang.Specification

class RemovingAccessToParkingSpotTest extends Specification {
  
  def "removing access to one-spaced parking spot ends in free parking spot"() {
    given:
      def parkedVehicle = VehicleId.of(1)
    and:
      def fullyOccupiedParkingSpot = new FullyOccupiedParkingSpot(ParkingSpotId.of(1), 1, [parkedVehicle])
    
    when:
      def result = fullyOccupiedParkingSpot.revokeAccessFrom parkedVehicle
    
    then:
      result in FreeParkingSpot
  }
  
  def "removing access to partially occupied parking spot with one vehicle ends in free parking spot"() {
    given:
      def parkedVehicle = VehicleId.of(1)
    and:
      def partiallyOccupiedParkingSpot = new PartiallyOccupiedParkingSpot(ParkingSpotId.of(1), 2, [parkedVehicle])
    
    when:
      def result = partiallyOccupiedParkingSpot.revokeAccessFrom parkedVehicle
    
    then:
      result in FreeParkingSpot
  }
  
}
