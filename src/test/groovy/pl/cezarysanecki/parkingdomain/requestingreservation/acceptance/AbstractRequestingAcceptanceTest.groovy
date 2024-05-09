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
    eventPublisher.publish(new ParkingSpotAdded(ParkingSpotId.newOne(), ParkingSpotCapacity.of(capacity), category))
  }
  
  ClientId registerClient(String phoneNumber) {
    eventPublisher.publish(new ClientRegistered(ClientId.newOne(), PhoneNumber.of(phoneNumber)))
  }
  
}
