package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application

import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated

class CreatingParkingSpotRequestsTest extends Specification {
  
  ParkingSpotRequestsRepository parkingSpotRequestsRepository = Mock()
  
  @Subject
  CreatingParkingSpotRequestsEventsHandler creatingParkingSpotRequestsEventsHandler = new CreatingParkingSpotRequestsEventsHandler(parkingSpotRequestsRepository)
  
  def "create parking spot requests when parking spot is created"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def parkingSpotCapacity = ParkingSpotCapacity.of(4)
    
    when:
      creatingParkingSpotRequestsEventsHandler.handle(new ParkingSpotCreated(parkingSpotId, parkingSpotCapacity, ParkingSpotCategory.Bronze))
    
    then:
      1 * parkingSpotRequestsRepository.createUsing(parkingSpotId, parkingSpotCapacity)
  }
  
}
