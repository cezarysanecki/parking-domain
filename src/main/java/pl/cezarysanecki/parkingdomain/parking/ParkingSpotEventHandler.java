package pl.cezarysanecki.parkingdomain.parking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.integration.ReservationRequestsConfirmed;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Slf4j
class ParkingSpotEventHandler {

  @EventListener
  public void handle(ClientRegistered event) {
    BeneficiaryId beneficiaryId = BeneficiaryId.of(event.clientId().getValue());
    log.debug("storing beneficiary with id: {}", beneficiaryId);
    beneficiaryRepository.save(beneficiaryId);
  }

  @EventListener
  public void handle(ParkingSpotAdded event) {
    ParkingSpotId parkingSpotId = event.parkingSpotId();
    log.debug("storing parking spot as placement with id: {}", parkingSpotId);
    parkingSpotRepository.saveNew(parkingSpotId, event.capacity(), event.category());
  }

  @EventListener
  public void handle(ReservationRequestsConfirmed event) {
    ParkingSpotId parkingSpotId = event.parkingSpotId();
    ReservationRequest reservationRequest = event.reservationRequest();

    BeneficiaryId beneficiaryId = BeneficiaryId.of(reservationRequest.getRequesterId().getValue());
    ReservationId reservationId = ReservationId.of(reservationRequest.getReservationRequestId().getValue());
    SpotUnits spotUnits = event.reservationRequest().getSpotUnits();

    log.debug("confirming reservation with id {} for parking spot id {}", reservationId, parkingSpotId);

    if (occupationRepository.containsOccupationFor(beneficiaryId, parkingSpotId)) {
      log.debug("beneficiary with id {} already occupies parking spot with id {}, skipping reservation", beneficiaryId, parkingSpotId);
    } else {
      reservationRepository.saveNew(reservationId, parkingSpotId, beneficiaryId, spotUnits);
      log.debug("successfully confirmed reservation with id {} for parking spot id {}", reservationId, parkingSpotId);
    }

  }

}
