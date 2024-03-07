package pl.cezarysanecki.parkingdomain.poc

import spock.lang.Specification

class ParkingSpotFactoryTest extends Specification {
  
  def parkingSpotId = ParkingSpotId.of(1)
  
  def "resolve free parking spot"() {
    when:
      def parkingSpot = ParkingSpot.resolve parkingSpotId, 1, []
    
    then:
      parkingSpot in FreeParkingSpot
  }
  
  def "resolve partially occupied parking spot"() {
    when:
      def parkingSpot = ParkingSpot.resolve parkingSpotId, 2, [VehicleId.of(1)]
    
    then:
      parkingSpot in PartiallyOccupiedParkingSpot
  }
  
  def "resolve fully occupied parking spot"() {
    when:
      def parkingSpot = ParkingSpot.resolve parkingSpotId, 1, [VehicleId.of(1)]
    
    then:
      parkingSpot in FullyOccupiedParkingSpot
  }
  
  def "resolve over occupied parking spot"() {
    when:
      def parkingSpot = ParkingSpot.resolve parkingSpotId, 1, [VehicleId.of(1), VehicleId.of(2)]
    
    then:
      parkingSpot in OverOccupiedParkingSpot
  }
  
}
