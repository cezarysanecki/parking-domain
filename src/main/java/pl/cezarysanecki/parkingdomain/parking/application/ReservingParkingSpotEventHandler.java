package pl.cezarysanecki.parkingdomain.parking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.integration.ReservationRequestsConfirmed;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;

@Slf4j
@RequiredArgsConstructor
public class ReservingParkingSpotEventHandler {

  private final BeneficiaryRepository beneficiaryRepository;
  private final ParkingSpotRepository parkingSpotRepository;

  @EventListener
  public void handle(ReservationRequestsConfirmed event) {
    ReservationRequest reservationRequest = event.reservationRequest();
    ParkingSpotId parkingSpotId = event.parkingSpotId();

    ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);

    Beneficiary beneficiary = findBeneficiaryBy(BeneficiaryId.of(reservationRequest.getRequesterId().getValue()));

    AppendingReservation.append(
            parkingSpot,
            beneficiary,
            reservationRequest)
        .onFailure(t -> log.error("cannot create reservation from request with id {}, reason {}", reservationRequest.getReservationRequestId(), t.getMessage()))
        .onSuccess(reservation -> log.error("created reservation with id {} from request with id {}", reservation.getReservationId(), reservationRequest.getReservationRequestId()));

    parkingSpotRepository.save(parkingSpot);
  }

  private ParkingSpot findParkingSpotBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
  }

  private Beneficiary findBeneficiaryBy(BeneficiaryId beneficiaryId) {
    return beneficiaryRepository.findBy(beneficiaryId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
  }

}
