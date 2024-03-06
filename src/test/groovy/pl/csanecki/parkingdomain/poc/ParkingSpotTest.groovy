package pl.csanecki.parkingdomain.poc

import spock.lang.Specification

class ParkingSpotTest extends Specification {
  
  def "test"() {
    given:
      def a = 1
      def b = 1
    
    when:
      def result = a + b
    
    then:
      result == 2
  }
  
}
