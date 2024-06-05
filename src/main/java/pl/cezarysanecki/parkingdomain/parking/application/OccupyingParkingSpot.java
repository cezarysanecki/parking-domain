package pl.cezarysanecki.parkingdomain.parking.application;

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
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.function.Function;
import java.util.function.Supplier;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

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
    return occupy(
        beneficiaryId,
        () -> findBy(parkingSpotId),
        parkingSpot -> parkingSpot.occupy(beneficiaryId, spotUnits));
  }

  public Try<Occupation> occupyWhole(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId
  ) {
    return occupy(
        beneficiaryId,
        () -> findBy(parkingSpotId),
        parkingSpot -> parkingSpot.occupyWhole(beneficiaryId));
  }

  public Try<Occupation> occupyAvailable(
      BeneficiaryId beneficiaryId,
      ParkingSpotCategory category,
      SpotUnits spotUnits
  ) {
    return occupy(
        beneficiaryId,
        () -> findAvailableFor(category, spotUnits),
        parkingSpot -> parkingSpot.occupyWhole(beneficiaryId));
  }

  private Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      Supplier<ParkingSpot> parkingSpotSupplier,
      Function<ParkingSpot, Either<ParkingSpotEvent.ParkingSpotOccupiedEvents, RuntimeException>> occupationFunction
  ) {
    return Try.of(() -> {
      if (!beneficiaryRepository.isPresent(beneficiaryId)) {
        throw new IllegalStateException("cannot find beneficiary with id " + beneficiaryId);
      }
      ParkingSpot parkingSpot = parkingSpotSupplier.get();

      log.debug("occupying parking spot with id {} by beneficiary with id {}", parkingSpot.getParkingSpotId(), beneficiaryId);
      var result = occupationFunction.apply(parkingSpot);

      return Match(result).of(
          Case($Left($()), event -> {
            parkingSpotRepository.publish(event);
            return event.occupied().occupation();
          }),
          Case($Right($()), exception -> {
            throw exception;
          })
      );
    }).onFailure(exception -> log.error("cannot occupy parking spot, reason: {}", exception.getMessage()));
  }

  private ParkingSpot findBy(ParkingSpotId parkingSpotId) {
    return parkingSpotRepository.findBy(parkingSpotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find parking spot with id " + parkingSpotId));
  }

  private ParkingSpot findAvailableFor(ParkingSpotCategory category, SpotUnits spotUnits) {
    return parkingSpotRepository.findAvailableFor(category, spotUnits)
        .getOrElseThrow(() -> new IllegalStateException("cannot find any parking spot with category " + category + " and having enough space"));
  }

}
