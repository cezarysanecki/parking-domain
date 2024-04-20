package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application


import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated

class CreatingParkingSpotReservationRequestsTest extends Specification {
  
  ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository = Mock()
  
  @Subject
  CreatingParkingSpotReservationRequestsEventsHandler creatingParkingSpotReservationRequestsEventsHandler = new CreatingParkingSpotReservationRequestsEventsHandler(parkingSpotReservationRequestsRepository)
  
  def "create parking spot reservation requests when parking spot is created"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
      def parkingSpotCapacity = ParkingSpotCapacity.of(4)
    
    when:
      creatingParkingSpotReservationRequestsEventsHandler.handle(new ParkingSpotCreated(parkingSpotId, parkingSpotCapacity, ParkingSpotCategory.Bronze))
    
    then:
      1 * parkingSpotReservationRequestsRepository.createUsing(parkingSpotId, parkingSpotCapacity)
  }
  
}
