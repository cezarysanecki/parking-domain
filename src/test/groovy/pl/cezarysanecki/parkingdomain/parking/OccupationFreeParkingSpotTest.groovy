package pl.cezarysanecki.parkingdomain.parking

import spock.lang.Specification

class OccupationFreeParkingSpotTest extends Specification {
  
  def "fully occupy one-spaced free parking spot after being occupied by vehicle"() {
    given:
      def freeParkingSpot = new FreeParkingSpot(ParkingSpotId.of(1), 1)
    
    when:
      def result = freeParkingSpot.occupyBy VehicleId.of(1)
    
    then:
      result in FullyOccupiedParkingSpot
  }
  
  def "partially occupy more then one-spaced free parking spot after after being occupied by vehicle"() {
    given:
      def freeParkingSpot = new FreeParkingSpot(ParkingSpotId.of(1), 2)
    
    when:
      def result = freeParkingSpot.occupyBy VehicleId.of(4)
    
    then:
      result in PartiallyOccupiedParkingSpot
  }
  
}
