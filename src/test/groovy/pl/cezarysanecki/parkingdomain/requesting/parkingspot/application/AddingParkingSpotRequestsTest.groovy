package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application

import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

class AddingParkingSpotRequestsTest extends Specification {
  
  ParkingSpotRequestsRepository parkingSpotRequestsRepository = Mock()
  
  @Subject
  CreatingParkingSpotRequestsEventsHandler creatingParkingSpotRequestsEventsHandler = new CreatingParkingSpotRequestsEventsHandler(parkingSpotRequestsRepository)
  
  def "create parking spot requests when parking spot is created"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def parkingSpotCapacity = ParkingSpotCapacity.of(4)
    
    when:
      creatingParkingSpotRequestsEventsHandler.handle(new ParkingSpotAdded(parkingSpotId, parkingSpotCapacity, ParkingSpotCategory.Bronze))
    
    then:
      1 * parkingSpotRequestsRepository.createUsing(parkingSpotId, parkingSpotCapacity)
  }
  
}
