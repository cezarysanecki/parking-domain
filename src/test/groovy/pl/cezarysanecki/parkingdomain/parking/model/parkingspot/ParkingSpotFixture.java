package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingSpot;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotFixture {

  public static ParkingSpot emptyParkingSpotWithCapacity(int capacity) {
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        ParkingSpotCapacity.of(capacity),
        0,
        HashMap.empty(),
        false,
        Version.zero());
  }

  public static ParkingSpot emptyParkingSpotWithReservationFor(BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
    ParkingSpotId parkingSpotId = ParkingSpotId.newOne();
    Reservation reservation = new Reservation(parkingSpotId, beneficiaryId, spotUnits);
    return new ParkingSpot(
        parkingSpotId,
        ParkingSpotCapacity.of(spotUnits.getValue()),
        0,
        HashMap.of(reservation.getReservationId(), reservation),
        false,
        Version.zero());
  }

  public static ParkingSpot fullyOccupiedParkingSpot() {
    ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4);
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        capacity,
        capacity.getValue(),
        HashMap.empty(),
        false,
        Version.zero());
  }

  public static ParkingSpot occupiedWithLeftSpace(int spaceLeft) {
    ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4 + spaceLeft);
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        capacity,
        capacity.getValue() - spaceLeft,
        HashMap.empty(),
        false,
        Version.zero());
  }

}
