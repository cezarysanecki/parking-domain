package pl.cezarysanecki.parkingdomain.parking.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@Slf4j
@RequiredArgsConstructor
public class OccupyingRandomParkingSpot {

  private final BeneficiaryRepository beneficiaryRepository;
  private final ParkingSpotRepository parkingSpotRepository;

  public Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      ParkingSpotCategory category,
      SpotUnits spotUnits
  ) {
    if (beneficiaryRepository.isPresent(beneficiaryId)) {
      return Try.failure(new IllegalStateException("cannot find beneficiary with id: " + beneficiaryId));
    }

    ParkingSpot parkingSpot = findAvailableFor(category, spotUnits);
    log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiaryId);

    return parkingSpot.occupy(beneficiaryId, spotUnits)
        .onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()))
        .onSuccess(parkingSpotRepository::publish)
        .map(ParkingSpotOccupied::occupation);
  }

  private ParkingSpot findAvailableFor(ParkingSpotCategory category, SpotUnits spotUnits) {
    return parkingSpotRepository.findAvailableFor(category, spotUnits)
        .getOrElseThrow(() -> new IllegalStateException("cannot find any parking spot with category: " + category + " and having enough space"));
  }

}
