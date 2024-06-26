package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.integration.ReservationRequestsConfirmed;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Slf4j
@RequiredArgsConstructor
public class ReservingParkingSpotEventHandler {

  private final OccupationRepository occupationRepository;
  private final ReservationRepository reservationRepository;

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
