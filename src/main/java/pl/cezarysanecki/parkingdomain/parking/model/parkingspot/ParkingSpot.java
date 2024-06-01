package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent.ParkingSpotOccupied;

@Getter
@AllArgsConstructor
public class ParkingSpot {

  @NonNull
  private final ParkingSpotId parkingSpotId;
  @NonNull
  private final ParkingSpotCapacity capacity;
  private final int usedSpace;
  @NonNull
  private final Map<ReservationId, Reservation> reservations;
  private boolean outOfUse;
  @NonNull
  private final Version version;

  public Try<ParkingSpotOccupied> occupyWhole(BeneficiaryId beneficiaryId) {
    return occupy(beneficiaryId, SpotUnits.of(capacity.getValue()));
  }

  public Try<ParkingSpotOccupied> occupyUsing(ReservationId reservationId) {
    Option<Reservation> reservation = reservations.get(reservationId);
    if (reservation.isEmpty()) {
      return Try.failure(new IllegalArgumentException("no such reservation"));
    }
    Reservation presentReservation = reservation.get();
    return occupy(presentReservation.getBeneficiaryId(), presentReservation.getSpotUnits());
  }

  public Try<ParkingSpotOccupied> occupy(BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
    if (outOfUse) {
      return Try.failure(new IllegalArgumentException("out of order"));
    }
    if (exceedsAllowedSpace(spotUnits)) {
      return Try.failure(new IllegalArgumentException("not enough space"));
    }

    return Try.of(() -> new ParkingSpotOccupied(
        parkingSpotId,
        Occupation.newOne(beneficiaryId, spotUnits)));
  }

  public Try<Result> putIntoService() {
    if (!outOfUse) {
      return Try.failure(new IllegalStateException("is already in service"));
    }
    this.outOfUse = false;
    return Try.success(Result.Success);
  }

  public Try<Result> makeOutOfUse() {
    if (outOfUse) {
      return Try.failure(new IllegalStateException("is already out of use"));
    }
    this.outOfUse = true;
    return Try.success(Result.Success);
  }

  public boolean isFull() {
    return currentOccupation() == capacity.getValue();
  }

  public int spaceLeft() {
    return capacity.getValue() - currentOccupation();
  }

  private int currentOccupation() {
    Integer reservationUnits = reservations.values()
        .map(Reservation::getSpotUnits)
        .map(SpotUnits::getValue)
        .reduceOption(Integer::sum)
        .getOrElse(0);
    return usedSpace + reservationUnits;
  }

  private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
    return spotUnits.getValue() + currentOccupation() > capacity.getValue();
  }

}
