package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryRepository;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.function.Function;
import java.util.function.Supplier;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupiedEvents;

@Slf4j
@RequiredArgsConstructor
class OccupyingParkingSpot {

  private final BeneficiaryRepository beneficiaryRepository;
  private final ParkingSpotRepository parkingSpotRepository;

  Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpotId, beneficiaryId);

    return occupy(
        beneficiaryId,
        () -> parkingSpotRepository.getBy(parkingSpotId),
        parkingSpot -> parkingSpot.occupy(beneficiaryId, spotUnits));
  }

  Try<Occupation> occupyWhole(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId
  ) {
    log.debug("occupying whole parking spot with id {} by beneficiary with id {}", parkingSpotId, beneficiaryId);

    return occupy(
        beneficiaryId,
        () -> parkingSpotRepository.getBy(parkingSpotId),
        parkingSpot -> parkingSpot.occupyWhole(beneficiaryId));
  }

  Try<Occupation> occupyAvailable(
      BeneficiaryId beneficiaryId,
      ParkingSpotCategory category,
      SpotUnits spotUnits
  ) {
    log.debug("occupying available parking spot with category {} by beneficiary with id {}", category, beneficiaryId);

    return occupy(
        beneficiaryId,
        () -> parkingSpotRepository.getAvailableFor(category, spotUnits),
        parkingSpot -> parkingSpot.occupy(beneficiaryId, spotUnits));
  }

  private Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      Supplier<ParkingSpot> parkingSpotSupplier,
      Function<ParkingSpot, Either<RuntimeException, ParkingSpotOccupiedEvents>> occupationFunction
  ) {
    return Try.of(() -> {
      if (!beneficiaryRepository.isPresent(beneficiaryId)) {
        throw new IllegalStateException("cannot find beneficiary with id " + beneficiaryId);
      }
      ParkingSpot parkingSpot = parkingSpotSupplier.get();

      log.debug("try to occupy found parking spot with id {} by found beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiaryId);
      var result = occupationFunction.apply(parkingSpot);

      return Match(result).of(
          Case($Right($()), event -> {
            parkingSpotRepository.publish(event);
            log.debug("successfully occupied parking spot with id {}", parkingSpot.getParkingSpotId());
            return event.occupied().occupation();
          }),
          Case($Left($()), exception -> {
            throw exception;
          })
      );
    }).onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()));
  }

}
