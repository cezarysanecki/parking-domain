package pl.cezarysanecki.parkingdomain.parking.parkingspot.application


import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import spock.lang.Specification
import spock.lang.Subject

class CreatingParkingSpotTest extends Specification {
  
  ParkingSpots parkingSpots = Mock()
  
  @Subject
  CreatingParkingSpot creatingParkingSpot = new CreatingParkingSpot(parkingSpots)
  
  def "allow to create parking spot"() {
    when:
      def result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
          ParkingSpotCapacity.of(4), ParkingSpotCategory.Gold))
    
    then:
      result.isSuccess()
  }
  
  def "inform that parking spot has been created"() {
    given:
      def parkingSpotCapacity = ParkingSpotCapacity.of(4)
      def parkingSpotCategory = ParkingSpotCategory.Gold
    
    when:
      creatingParkingSpot.create(new CreatingParkingSpot.Command(
          parkingSpotCapacity, parkingSpotCategory))
    
    then:
      1 * parkingSpots.publish({
        it.parkingSpotCapacity == parkingSpotCapacity
            && it.parkingSpotCategory == parkingSpotCategory
      } as CreatingParkingSpot.ParkingSpotCreated)
  }
  
  def "fail to create parking spot when publishing fails"() {
    given:
      parkingSpots.publish(_ as CreatingParkingSpot.ParkingSpotCreated) >> {
        throw new IllegalStateException()
      }
    
    when:
      def result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
          ParkingSpotCapacity.of(4), ParkingSpotCategory.Gold))
    
    then:
      result.isFailure()
  }
  
}
