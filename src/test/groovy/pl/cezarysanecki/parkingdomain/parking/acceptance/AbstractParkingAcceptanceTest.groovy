package pl.cezarysanecki.parkingdomain.parking.acceptance

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
import pl.cezarysanecki.parkingdomain.parking.infrastructure.ParkingSpotConfig
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.application.ExchangingReservationRequestsTimeSlots
import pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.RequestingReservationTimeSlotConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.integration.ReservationRequestsConfirmed
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits
import spock.lang.Specification

import java.time.Instant

import static pl.cezarysanecki.parkingdomain.management.client.ClientRegistered.IndividualClientRegistered

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ParkingSpotConfig.class,
    RequestingReservationTimeSlotConfig.class,
    EventPublisherTestConfig.class,
    LocalConfig.class])
abstract class AbstractParkingAcceptanceTest extends Specification {
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ExchangingReservationRequestsTimeSlots creatingReservationRequestTimeSlots
  
  void createTimeSlots() {
    creatingReservationRequestTimeSlots.exchangeTimeSlots(Instant.now())
  }
  
  ParkingSpotId addParkingSpot(int capacity, ParkingSpotCategory category) {
    def parkingSpotId = ParkingSpotId.newOne()
    eventPublisher.publish(new ParkingSpotAdded(parkingSpotId, ParkingSpotCapacity.of(capacity), category))
    return parkingSpotId
  }
  
  BeneficiaryId registerBeneficiary() {
    def clientId = ClientId.newOne()
    eventPublisher.publish(new IndividualClientRegistered(clientId))
    return BeneficiaryId.of(clientId.value)
  }
  
  ReservationId reserveParkingSpot(ParkingSpotId parkingSpotId, ReservationRequesterId requesterId, SpotUnits spotUnits) {
    def reservationRequest = new ReservationRequest(requesterId, ReservationRequestsTimeSlotId.newOne(), spotUnits)
    eventPublisher.publish(new ReservationRequestsConfirmed(parkingSpotId, reservationRequest))
    return ReservationId.of(reservationRequest.getReservationRequestId().value)
  }
  
}
