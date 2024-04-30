package pl.cezarysanecki.parkingdomain.parking.parkingspot.application

import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.AddingParkingSpot
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import spock.lang.Specification
import spock.lang.Subject

class AddingParkingSpotTest extends Specification {
  
  ParkingSpots parkingSpots = Mock()
  
  @Subject
  AddingParkingSpot creatingParkingSpot = new AddingParkingSpot(parkingSpots)
  
  def "allow to create parking spot"() {
    given:
      def parkingSpotCapacity = ParkingSpotCapacity.of(4)
      def parkingSpotCategory = ParkingSpotCategory.Gold
      
      final Command command = new Command(parkingSpotCapacity, parkingSpotCategory)
    when:
      def result = creatingParkingSpot.addParkingSpot(command.parkingSpotCapacity, command.parkingSpotCategory)
    
    then:
      result.isSuccess()
    and:
      1 * parkingSpots.publish({
        it.parkingSpotCapacity == parkingSpotCapacity
            && it.parkingSpotCategory == parkingSpotCategory
      } as ParkingSpotAdded)
  }
  
  def "fail to create parking spot when publishing fails"() {
    given:
      parkingSpots.publish(_ as ParkingSpotAdded) >> {
        throw new IllegalStateException()
      }
      
      final Command command = new Command(
          ParkingSpotCapacity.of(4), ParkingSpotCategory.Gold)
    when:
      def result = creatingParkingSpot.addParkingSpot(command.parkingSpotCapacity, command.parkingSpotCategory)
    
    then:
      result.isFailure()
  }
  
}
