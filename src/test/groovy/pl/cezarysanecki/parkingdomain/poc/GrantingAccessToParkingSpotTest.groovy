package pl.cezarysanecki.parkingdomain.poc

import spock.lang.Specification

class GrantingAccessToParkingSpotTest extends Specification {
  
  def "fully occupy one-space free parking spot after granting access for vehicle"() {
    given:
      def freeParkingSpot = new FreeParkingSpot(ParkingSpotId.of(1), 1)
    
    when:
      def result = freeParkingSpot.grantAccessFor VehicleId.of(1)
    
    then:
      result in FullyOccupiedParkingSpot
  }
  
  def "fully occupy #capacityTestCase partially occupied parking spot after granting access to last space"() {
    given:
      def partiallyOccupiedParkingSpot = new PartiallyOccupiedParkingSpot(ParkingSpotId.of(1), capacity, parkedVehicles)
    
    when:
      def result = partiallyOccupiedParkingSpot.grantAccessFor VehicleId.of(4)
    
    then:
      result in FullyOccupiedParkingSpot
    
    where:
      capacityTestCase | capacity | parkedVehicles
      "two-spaced"     | 2        | [VehicleId.of(1)]
      "three-spaced"   | 3        | [VehicleId.of(1), VehicleId.of(2)]
      "four-spaced"    | 4        | [VehicleId.of(1), VehicleId.of(2), VehicleId.of(3)]
  }
  
}
