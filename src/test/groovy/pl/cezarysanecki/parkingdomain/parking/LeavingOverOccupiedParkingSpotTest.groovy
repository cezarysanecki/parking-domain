package pl.cezarysanecki.parkingdomain.parking

import spock.lang.Specification

class LeavingOverOccupiedParkingSpotTest extends Specification {
  
  def "still be over occupied parking spot after being left by one of vehicles and have more vehicles then capacity"() {
    given:
      def vehicle = VehicleId.of(1)
    and:
      def overOccupiedParkingSpot = new OverOccupiedParkingSpot(ParkingSpotId.of(1), 1, [vehicle, VehicleId.of(2), VehicleId.of(3)].toSet())
    
    when:
      def result = overOccupiedParkingSpot.leaveBy vehicle
    
    then:
      result in OverOccupiedParkingSpot
  }
  
  def "over occupied parking spot should be fully occupied after being left by one of vehicles and have amount of vehicles equaled to capacity"() {
    given:
      def vehicle = VehicleId.of(1)
    and:
      def overOccupiedParkingSpot = new OverOccupiedParkingSpot(ParkingSpotId.of(1), 1, [vehicle, VehicleId.of(2)].toSet())
    
    when:
      def result = overOccupiedParkingSpot.leaveBy vehicle
    
    then:
      result in FullyOccupiedParkingSpot
  }
  
}
