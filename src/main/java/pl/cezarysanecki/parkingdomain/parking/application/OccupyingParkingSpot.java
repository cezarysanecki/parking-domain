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
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupiedEvents;

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
    if (!beneficiaryRepository.isPresent(beneficiaryId)) {
      return Try.failure(new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
    }

    ParkingSpot parkingSpot = findBy(parkingSpotId);
    log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiaryId);

    return parkingSpot.occupy(beneficiaryId, spotUnits)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupiedEvents::occupied)
        .map(ParkingSpotOccupied::occupation);
  }

  public Try<Occupation> occupyWhole(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId
  ) {
    if (!beneficiaryRepository.isPresent(beneficiaryId)) {
      return Try.failure(new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
    }

    ParkingSpot parkingSpot = findBy(parkingSpotId);
    log.debug("occupying whole parking spot with id {} by beneficiary with id {}", parkingSpotId, beneficiaryId);

    return parkingSpot.occupyWhole(beneficiaryId)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupiedEvents::occupied)
        .map(ParkingSpotOccupied::occupation);
  }

  private ParkingSpot findBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id: " + parkingSpotId));
  }

}
