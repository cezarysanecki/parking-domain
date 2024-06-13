package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.integration.ReservationRequestsConfirmed;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Slf4j
@RequiredArgsConstructor
public class ReservingParkingSpotEventHandler {

  private final ReservationRepository reservationRepository;

  @EventListener
  public void handle(ReservationRequestsConfirmed event) {
    ParkingSpotId parkingSpotId = event.parkingSpotId();
    ReservationRequest reservationRequest = event.reservationRequest();

    BeneficiaryId beneficiaryId = BeneficiaryId.of(reservationRequest.getRequesterId().getValue());
    ReservationId reservationId = ReservationId.of(reservationRequest.getReservationRequestId().getValue());
    SpotUnits spotUnits = event.reservationRequest().getSpotUnits();

    log.debug("confirming reservation with id {} for parking spot id {}", reservationId, parkingSpotId);

    reservationRepository.saveNew(reservationId, parkingSpotId, beneficiaryId, spotUnits);
  }

}
