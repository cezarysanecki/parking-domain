package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered
import pl.cezarysanecki.parkingdomain.management.client.PhoneNumber
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.RequestingReservationConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [
    RequestingReservationConfig.class,
    EventPublisherTestConfig.class])
abstract class AbstractRequestingAcceptanceTest extends Specification {
  
  @Autowired
  EventPublisher eventPublisher
  
  ParkingSpotId addParkingSpot(int capacity, ParkingSpotCategory category) {
    def parkingSpotId = ParkingSpotId.newOne()
    eventPublisher.publish(new ParkingSpotAdded(parkingSpotId, ParkingSpotCapacity.of(capacity), category))
    return parkingSpotId
  }
  
  ReservationRequesterId registerRequester(String phoneNumber) {
    def clientId = ClientId.newOne()
    eventPublisher.publish(new ClientRegistered(clientId, PhoneNumber.of(phoneNumber)))
    return ReservationRequesterId.of(clientId.value)
  }
  
}
