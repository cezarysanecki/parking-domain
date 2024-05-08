package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application

import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId

import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

class AddingParkingSpotReservationRequestsTest extends Specification {
  
  ParkingSpotReservationRequestsRepository parkingSpotRequestsRepository = Mock()
  
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
