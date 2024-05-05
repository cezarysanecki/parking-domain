package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;

public class ParkingSpot {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private Set<Occupations> occupations;
    @NonNull
    private Set<Reservation> reservations;
    @NonNull
    private final Version version;

    public ParkingSpot(
            ParkingSpotId parkingSpotId,
            ParkingSpotCapacity capacity,
            Map<OccupationId, SpotUnits> occupations,
            Map<ReservationId, SpotUnits> reservations,
            Version version) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.occupations = occupations.map(entry -> new Occupations(entry._1, entry._2)).toSet();
        this.reservations = reservations.map(entry -> new Reservation(entry._1, entry._2)).toSet();
        this.version = version;
    }

    public Try<OccupationId> occupy(SpotUnits spotUnits) {
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalArgumentException("not enough space"));
        }
        Occupations occupations = new Occupations(OccupationId.newOne(), spotUnits);
        this.occupations = this.occupations.add(occupations);

        return Try.of(() -> occupations.occupationId);
    }

    public Try<OccupationId> occupy(ReservationId reservationId) {
        Option<Reservation> presentReservation = reservations
                .find(reservation -> reservation.reservationId.equals(reservationId));
        if (presentReservation.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such reservation"));
        }

        Reservation reservation = presentReservation.get();
        reservations = reservations.remove(reservation);

        Occupations occupations = new Occupations(OccupationId.newOne(), reservation.spotUnits);
        this.occupations = this.occupations.add(occupations);

        return Try.of(() -> occupations.occupationId);
    }

    public Try<OccupationId> release(OccupationId occupationId) {
        Option<Occupations> presentOccupation = this.occupations
                .find(occupations -> occupations.occupationId.equals(occupationId));
        if (presentOccupation.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such occupation"));
        }

        Occupations occupations = presentOccupation.get();
        this.occupations = this.occupations.remove(occupations);

        return Try.of(() -> occupations.occupationId);
    }

    private Integer currentOccupation() {
        Integer occupationUnits = occupations
                .map(Occupations::spotUnits)
                .map(SpotUnits::getValue)
                .reduce(Integer::sum);
        Integer reservationUnits = reservations
                .map(Reservation::spotUnits)
                .map(SpotUnits::getValue)
                .reduce(Integer::sum);
        return occupationUnits + reservationUnits;
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentOccupation() > capacity.getValue();
    }

}
