package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

@Getter
@AllArgsConstructor
class ParkingSpot {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private Map<OccupationId, SpotUnits> occupations;
    @NonNull
    private Map<ReservationId, SpotUnits> reservations;
    @NonNull
    private final Version version;

    Try<OccupationId> occupy(SpotUnits spotUnits) {
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalArgumentException("not enough space"));
        }

        OccupationId occupationId = OccupationId.newOne();
        occupations = occupations.put(occupationId, spotUnits);

        return Try.of(() -> occupationId);
    }

    Try<OccupationId> occupyAll() {
        if (currentOccupation() != 0) {
            return Try.failure(new IllegalArgumentException("not fully released"));
        }

        OccupationId occupationId = OccupationId.newOne();
        occupations = occupations.put(occupationId, SpotUnits.of(capacity.getValue()));

        return Try.of(() -> occupationId);
    }

    Try<OccupationId> occupy(ReservationId reservationId) {
        Option<SpotUnits> reservedUnits = reservations.get(reservationId);
        if (reservations.containsKey(reservationId)) {
            return Try.failure(new IllegalArgumentException("no such reservation"));
        }

        reservations = reservations.remove(reservationId);

        OccupationId occupationId = OccupationId.newOne();
        occupations = occupations.put(occupationId, reservedUnits.get());

        return Try.of(() -> occupationId);
    }

    Try<OccupationId> release(OccupationId occupationId) {
        Option<SpotUnits> occupiedUnits = occupations.get(occupationId);
        if (occupiedUnits.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such occupation"));
        }

        occupations = occupations.remove(occupationId);

        return Try.of(() -> occupationId);
    }

    boolean isFull() {
        return currentOccupation() == capacity.getValue();
    }


    int spaceLeft() {
        return capacity.getValue() - currentOccupation();
    }

    private int currentOccupation() {
        Integer occupationUnits = occupations.values()
                .map(SpotUnits::getValue)
                .reduce(Integer::sum);
        Integer reservationUnits = reservations.values()
                .map(SpotUnits::getValue)
                .reduce(Integer::sum);
        return occupationUnits + reservationUnits;
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentOccupation() > capacity.getValue();
    }

}
