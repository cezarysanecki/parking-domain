package pl.cezarysanecki.parkingdomain.parking

import spock.lang.Specification

class OccupationPartiallyOccupiedParkingSpotTest extends Specification {
  
  def "fully occupy #capacityTestCase partially occupied parking spot after occupation of last space"() {
    given:
      def partiallyOccupiedParkingSpot = new PartiallyOccupiedParkingSpot(ParkingSpotId.of(1), capacity, parkedVehicles)
    
    when:
      def result = partiallyOccupiedParkingSpot.occupyBy VehicleId.of(4)
    
    then:
      result in FullyOccupiedParkingSpot
    
    where:
      capacityTestCase | capacity | parkedVehicles
      "two-spaced"     | 2        | [VehicleId.of(1)].toSet()
      "three-spaced"   | 3        | [VehicleId.of(1), VehicleId.of(2)].toSet()
      "four-spaced"    | 4        | [VehicleId.of(1), VehicleId.of(2), VehicleId.of(3)].toSet()
  }
  
  def "partially occupy partially occupied parking spot after being occupied by vehicle"() {
    given:
      def freeParkingSpot = new PartiallyOccupiedParkingSpot(ParkingSpotId.of(1), 3, [VehicleId.of(1)].toSet())
    
    when:
      def result = freeParkingSpot.occupyBy VehicleId.of(2)
    
    then:
      result in PartiallyOccupiedParkingSpot
  }
  
}
