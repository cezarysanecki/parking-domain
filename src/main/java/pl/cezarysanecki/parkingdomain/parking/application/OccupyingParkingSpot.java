package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.Beneficiary;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvents.ParkingSpotOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Slf4j
@RequiredArgsConstructor
public class OccupyingParkingSpot {

  private final EventPublisher eventPublisher;
  private final BeneficiaryRepository beneficiaryRepository;
  private final ParkingSpotRepository parkingSpotRepository;

  public Try<Occupation> occupyAny(
      BeneficiaryId beneficiaryId,
      ParkingSpotCategory category,
      SpotUnits spotUnits
  ) {
    ParkingSpot parkingSpot = findAvailableParkingSpotBy(category);
    log.debug("found parking spot with id {} for category {}", parkingSpot.getParkingSpotId(), category);
    return occupy(beneficiaryId, parkingSpot, spotUnits);
  }

  public Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);
    return occupy(beneficiaryId, parkingSpot, spotUnits);
  }

  public Try<Occupation> occupy(ReservationId reservationId) {
    log.debug("occupying parking spot using reservation with id {}", reservationId);

    ParkingSpot parkingSpot = findParkingSpotBy(reservationId);

    Try<Occupation> occupationResult = parkingSpot.occupyUsing(reservationId);
    if (occupationResult.isFailure()) {
      log.error("cannot occupy parking spot, reason: {}", occupationResult.getCause().getMessage());
      return occupationResult;
    }

    Occupation occupation = occupationResult.get();
    Beneficiary beneficiary = findBeneficiaryBy(occupation.getBeneficiaryId());

    log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiary.getBeneficiaryId());

    return beneficiary.append(occupation)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(processedOccupation -> {
          beneficiaryRepository.save(beneficiary);
          parkingSpotRepository.save(parkingSpot);

          eventPublisher.publish(new ParkingSpotOccupied(parkingSpot.getParkingSpotId(), processedOccupation));
        });
  }

  public Try<Occupation> occupyWhole(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId
  ) {
    log.debug("occupying whole parking spot with id {} by beneficiary with id {}", parkingSpotId, beneficiaryId);

    Beneficiary beneficiary = findBeneficiaryBy(beneficiaryId);
    ParkingSpot parkingSpot = findParkingSpotBy(parkingSpotId);

    return parkingSpot.occupyWhole(beneficiaryId)
        .flatMap(beneficiary::append)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(occupation -> {
          beneficiaryRepository.save(beneficiary);
          parkingSpotRepository.save(parkingSpot);

          eventPublisher.publish(new ParkingSpotOccupied(parkingSpotId, occupation));
        });
  }

  private Try<Occupation> occupy(BeneficiaryId beneficiaryId, ParkingSpot parkingSpot, SpotUnits spotUnits) {
    log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiaryId);
    Beneficiary beneficiary = findBeneficiaryBy(beneficiaryId);

    return parkingSpot.occupy(beneficiaryId, spotUnits)
        .flatMap(beneficiary::append)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(occupation -> {
          beneficiaryRepository.save(beneficiary);
          parkingSpotRepository.save(parkingSpot);

          eventPublisher.publish(new ParkingSpotOccupied(parkingSpot.getParkingSpotId(), occupation));
        });
  }

  private Beneficiary findBeneficiaryBy(BeneficiaryId beneficiaryId) {
    return beneficiaryRepository.findBy(beneficiaryId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
  }

  private ParkingSpot findParkingSpotBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
  }

  private ParkingSpot findParkingSpotBy(ReservationId reservationId) {
    return parkingSpotRepository.findBy(reservationId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot containing reservation with id: " + reservationId));
  }

  private ParkingSpot findAvailableParkingSpotBy(ParkingSpotCategory parkingSpotCategory) {
    return parkingSpotRepository.findAvailableBy(parkingSpotCategory)
        .getOrElseThrow(() -> new IllegalStateException("cannot find available parking spot by category: " + parkingSpotCategory));
  }

}
