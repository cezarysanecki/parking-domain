package pl.cezarysanecki.parkingdomain.parking.model.parkingspot;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.beneficiary.BeneficiaryId;
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

@Getter
@AllArgsConstructor
public class ParkingSpot {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private Map<OccupationId, Occupation> occupations;
    @NonNull
    private Map<ReservationId, Reservation> reservations;
    @NonNull
    private final Version version;

    public static ParkingSpot newOne(ParkingSpotCapacity capacity) {
        return new ParkingSpot(
                ParkingSpotId.newOne(),
                capacity,
                HashMap.empty(),
                HashMap.empty(),
                Version.zero());
    }

    public Try<Occupation> occupy(BeneficiaryId beneficiaryId, SpotUnits spotUnits) {
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalArgumentException("not enough space"));
        }

        Occupation occupation = Occupation.newOne(beneficiaryId, spotUnits);
        occupations = occupations.put(occupation.getOccupationId(), occupation);

        return Try.of(() -> occupation);
    }

    public Try<Occupation> occupyUsing(ReservationId reservationId) {
        Option<Reservation> potentialReservation = reservations.get(reservationId);
        if (reservations.containsKey(reservationId)) {
            return Try.failure(new IllegalArgumentException("no such reservation"));
        }
        Reservation reservation = potentialReservation.get();

        reservations = reservations.remove(reservationId);

        Occupation occupation = Occupation.newOne(
                reservation.getBeneficiaryId(),
                reservation.getSpotUnits());
        occupations = occupations.put(occupation.getOccupationId(), occupation);

        return Try.of(() -> occupation);
    }

    public Try<Occupation> occupyWhole(BeneficiaryId beneficiaryId) {
        if (currentOccupation() != 0) {
            return Try.failure(new IllegalArgumentException("not fully released"));
        }

        Occupation occupation = Occupation.newOne(beneficiaryId, SpotUnits.of(capacity.getValue()));
        occupations = occupations.put(occupation.getOccupationId(), occupation);

        return Try.of(() -> occupation);
    }

    public Try<Occupation> release(OccupationId occupationId) {
        Option<Occupation> potentialOccupation = occupations.get(occupationId);
        if (potentialOccupation.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such occupation"));
        }
        Occupation occupation = potentialOccupation.get();

        occupations = occupations.remove(occupationId);

        return Try.of(() -> occupation);
    }

    public boolean isFull() {
        return currentOccupation() == capacity.getValue();
    }

    public int spaceLeft() {
        return capacity.getValue() - currentOccupation();
    }

    private int currentOccupation() {
        Integer occupationUnits = occupations.values()
                .map(Occupation::getSpotUnits)
                .map(SpotUnits::getValue)
                .reduce(Integer::sum);
        Integer reservationUnits = reservations.values()
                .map(Reservation::getSpotUnits)
                .map(SpotUnits::getValue)
                .reduce(Integer::sum);
        return occupationUnits + reservationUnits;
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentOccupation() > capacity.getValue();
    }

}
