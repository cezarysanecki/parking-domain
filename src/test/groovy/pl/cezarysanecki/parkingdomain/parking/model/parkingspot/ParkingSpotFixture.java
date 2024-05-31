package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.Occupation;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.Reservation;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotFixture {

  public static ParkingSpot emptyParkingSpotWithCapacity(int capacity) {
    return ParkingSpot.newOne(ParkingSpotId.newOne(), ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Silver);
  }

  public static ParkingSpot emptyParkingSpotWithReservation(Reservation reservation) {
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        ParkingSpotCapacity.of(reservation.getSpotUnits().getValue()),
        ParkingSpotCategory.Gold,
        HashMap.empty(),
        HashMap.of(reservation.getReservationId(), reservation),
        false,
        Version.zero());
  }

  public static ParkingSpot fullyOccupiedParkingSpot() {
    ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4);
    Occupation occupation = Occupation.newOne(BeneficiaryId.newOne(), SpotUnits.of(capacity.getValue()));
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        capacity,
        ParkingSpotCategory.Gold,
        HashMap.of(occupation.getOccupationId(), occupation),
        HashMap.empty(),
        false,
        Version.zero());
  }

  public static ParkingSpot occupiedFullyBy(Occupation occupation) {
    ParkingSpotCapacity capacity = ParkingSpotCapacity.of(occupation.getSpotUnits().getValue());
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        capacity,
        ParkingSpotCategory.Gold,
        HashMap.of(occupation.getOccupationId(), occupation),
        HashMap.empty(),
        false,
        Version.zero());
  }

  public static ParkingSpot occupiedPartiallyBy(Occupation occupation) {
    ParkingSpotCapacity capacity = ParkingSpotCapacity.of(occupation.getSpotUnits().getValue() + 1);
    return new ParkingSpot(
        ParkingSpotId.newOne(),
        capacity,
        ParkingSpotCategory.Gold,
        HashMap.of(occupation.getOccupationId(), occupation),
        HashMap.empty(),
        false,
        Version.zero());
  }

}
