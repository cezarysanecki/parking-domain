package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Slf4j
@RequiredArgsConstructor
public class OccupyingParkingSpot {

  private final BeneficiaryRepository beneficiaryRepository;
  private final ParkingSpotRepository parkingSpotRepository;

  public Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    if (beneficiaryRepository.isPresent(beneficiaryId)) {
      return Try.failure(new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
    }

    ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);
    log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiaryId);

    return parkingSpot.occupy(beneficiaryId, spotUnits)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupied::occupation);
  }

  public Try<Occupation> occupyWhole(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId
  ) {
    if (beneficiaryRepository.isPresent(beneficiaryId)) {
      return Try.failure(new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
    }

    ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);
    log.debug("occupying whole parking spot with id {} by beneficiary with id {}", parkingSpotId, beneficiaryId);

    return parkingSpot.occupyWhole(beneficiaryId)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupied::occupation);
  }

  public Try<Occupation> occupy(ReservationId reservationId) {
    ParkingSpot parkingSpot = findParkingSpotBy(reservationId);
    log.debug("occupying parking spot using reservation with id {}", reservationId);

    return parkingSpot.occupyUsing(reservationId)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupied::occupation);
  }

  private ParkingSpot findParkingSpotBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
  }

  private ParkingSpot findParkingSpotBy(ReservationId reservationId) {
    return parkingSpotRepository.findBy(reservationId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot containing reservation with id: " + reservationId));
  }

}
