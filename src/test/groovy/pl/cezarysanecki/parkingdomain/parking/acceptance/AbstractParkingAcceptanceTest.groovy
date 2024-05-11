package pl.cezarysanecki.parkingdomain.parking.acceptance

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
import pl.cezarysanecki.parkingdomain.parking.infrastructure.ParkingSpotConfig
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.RequestingReservationConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ValidReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ParkingSpotConfig.class,
    RequestingReservationConfig.class,
    EventPublisherTestConfig.class])
abstract class AbstractParkingAcceptanceTest extends Specification {
  
  @Autowired
  EventPublisher eventPublisher
  
  ParkingSpotId addParkingSpot(int capacity, ParkingSpotCategory category) {
    def parkingSpotId = ParkingSpotId.newOne()
    eventPublisher.publish(new ParkingSpotAdded(parkingSpotId, ParkingSpotCapacity.of(capacity), category))
    return parkingSpotId
  }
  
  BeneficiaryId registerBeneficiary(String phoneNumber) {
    def clientId = ClientId.newOne()
    eventPublisher.publish(new ClientRegistered(clientId, PhoneNumber.of(phoneNumber)))
    return BeneficiaryId.of(clientId.value)
  }
  
  ReservationId reserveParkingSpot(ParkingSpotId parkingSpotId, ReservationRequesterId requesterId, SpotUnits spotUnits) {
    def validReservationRequest = ValidReservationRequest.from(
        ReservationRequest.newOne(requesterId, spotUnits))
    eventPublisher.publish(new ReservationRequestConfirmed(parkingSpotId, validReservationRequest))
    return ReservationId.of(validReservationRequest.reservationRequestId.value)
  }
  
}
