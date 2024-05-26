package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain._local.config.LocalConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.application.ExchangingReservationRequestsTimeSlots
import pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.RequestingReservationTimeSlotConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.management.client.ClientRegistered.IndividualClientRegistered

@ActiveProfiles("local")
@SpringBootTest(classes = [
    RequestingReservationTimeSlotConfig.class,
    EventPublisherTestConfig.class,
    LocalConfig.class])
abstract class AbstractRequestingAcceptanceTest extends Specification {
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ExchangingReservationRequestsTimeSlots creatingReservationRequestTimeSlots
  
  ParkingSpotId addParkingSpot(int capacity, ParkingSpotCategory category) {
    def parkingSpotId = ParkingSpotId.newOne()
    eventPublisher.publish(new ParkingSpotAdded(parkingSpotId, ParkingSpotCapacity.of(capacity), category))
    return parkingSpotId
  }
  
  void createTimeSlots() {
    creatingReservationRequestTimeSlots.exchangeTimeSlots(creatingReservationRequestTimeSlots.dateProvider.tomorrow())
  }
  
  ReservationRequesterId registerLowLimitRequester() {
    def clientId = ClientId.newOne()
    eventPublisher.publish(new IndividualClientRegistered(clientId))
    return ReservationRequesterId.of(clientId.value)
  }
  
}
