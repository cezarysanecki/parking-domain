package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.OccupyingReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationEvent;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
public class ParkingSpotFacade {

  private final OccupyingParkingSpot occupyingParkingSpot;
  private final OccupyingReservedParkingSpot occupyingReservedParkingSpot;
  private final ReleasingOccupation releasingOccupation;

  public Try<Occupation> occupy(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
    return occupyingParkingSpot.occupy(beneficiaryId, parkingSpotId, spotUnits);
  }

  public Try<Occupation> occupyWhole(
      BeneficiaryId beneficiaryId,
      ParkingSpotId parkingSpotId
  ) {
    return occupyingParkingSpot.occupyWhole(beneficiaryId, parkingSpotId);
  }

  public Try<Occupation> occupyAvailable(
      BeneficiaryId beneficiaryId,
      ParkingSpotCategory category,
      SpotUnits spotUnits
  ) {
    return occupyingParkingSpot.occupyAvailable(beneficiaryId, category, spotUnits);
  }

  public Try<Occupation> occupy(ReservationId reservationId) {
    return occupyingReservedParkingSpot.occupy(reservationId);
  }

  public Try<Occupation> release(OccupationId occupationId) {
    return releasingOccupation.release(occupationId);
  }

}
