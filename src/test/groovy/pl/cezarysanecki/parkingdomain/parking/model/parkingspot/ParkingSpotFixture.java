package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotFixture {

    public static ParkingSpot emptyParkingSpotWithCapacity(int capacity) {
        return ParkingSpot.newOne(ParkingSpotCapacity.of(capacity));
    }

    public static ParkingSpot emptyParkingSpotWithReservation(Reservation reservation) {
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                ParkingSpotCapacity.of(reservation.getSpotUnits().getValue()),
                HashMap.empty(),
                HashMap.of(reservation.getReservationId(), reservation),
                Version.zero());
    }

    public static ParkingSpot fullyOccupiedParkingSpot() {
        ParkingSpotCapacity capacity = ParkingSpotCapacity.of(4);
        Occupation occupation = Occupation.newOne(BeneficiaryId.newOne(), SpotUnits.of(capacity.getValue()));
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                capacity,
                HashMap.of(occupation.getOccupationId(), occupation),
                HashMap.empty(),
                Version.zero());
    }

    public static ParkingSpot fullyOccupiedBy(BeneficiaryId beneficiary) {
        ParkingSpot parkingSpot = ParkingSpot.newOne(ParkingSpotCapacity.of(4));

        parkingSpot.occupyWhole(beneficiary);

        return parkingSpot;
    }

    public static ParkingSpot occupiedFullyBy(Occupation occupation) {
        ParkingSpotCapacity capacity = ParkingSpotCapacity.of(occupation.getSpotUnits().getValue());
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                capacity,
                HashMap.of(occupation.getOccupationId(), occupation),
                HashMap.empty(),
                Version.zero());
    }

    public static ParkingSpot occupiedPartiallyBy(Occupation occupation) {
        ParkingSpotCapacity capacity = ParkingSpotCapacity.of(occupation.getSpotUnits().getValue() + 1);
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                capacity,
                HashMap.of(occupation.getOccupationId(), occupation),
                HashMap.empty(),
                Version.zero());
    }

}
