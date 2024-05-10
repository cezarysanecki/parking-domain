package pl.cezarysanecki.parkingdomain.management.parkingspot

import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import spock.lang.Specification
import spock.lang.Subject

class AddingParkingSpotTest extends Specification {
  
  CatalogueParkingSpotDatabase database = Mock()
  EventPublisher eventPublisher = Mock()
  
  @Subject
  AddingParkingSpot addingParkingSpot = new AddingParkingSpot(database, eventPublisher)
  
  def "allow to add parking spot"() {
    given:
      def parkingSpotCapacity = ParkingSpotCapacity.of(4)
      def parkingSpotCategory = ParkingSpotCategory.Gold
    
    when:
      def result = addingParkingSpot.addParkingSpot(parkingSpotCapacity.getValue(), parkingSpotCategory)
    
    then:
      result.isSuccess()
    and:
      1 * database.saveNew({
        it.capacity == parkingSpotCapacity
            && it.category == parkingSpotCategory
      } as ParkingSpot)
    and:
      1 * eventPublisher.publish({
        it.capacity == parkingSpotCapacity
            && it.category == parkingSpotCategory
      } as ParkingSpotAdded)
  }
  
  def "fail to add parking spot when storing in database"() {
    given:
      database.saveNew(_ as ParkingSpot) >> {
        throw new IllegalStateException()
      }
    
    when:
      def result = addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold)
    
    then:
      result.isFailure()
  }
  
}
