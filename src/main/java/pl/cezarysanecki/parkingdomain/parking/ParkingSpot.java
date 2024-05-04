package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;

import java.util.Optional;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.CompletelyReleased;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.FullyOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupationFailed;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Occupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.OccupiedEvents.events;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Released;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasedEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ReleasingFailed;

@Value
public class ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    @NonNull
    Set<Occupation> occupations;
    @NonNull
    Set<Reservation> reservations;
    @NonNull
    ParkingSpotCapacity capacity;

    public Try<OccupationId> occupy(SpotUnits spotUnits) {
        if (spotUnits.getValue() + currentOccupation() > capacity.getValue()) {
            return Try.failure(new IllegalArgumentException("not enough space"));
        }
        Occupation occupation = new Occupation(OccupationId.newOne(), spotUnits);
        occupations.add(occupation);

        return Try.of(() -> occupation.occupationId);
    }

    public Either<OccupationFailed, OccupiedEvents> occupy(ReservationId reservationId) {
        Optional<Reservation> presentReservation = reservations.stream()
                .filter(reservation -> reservation.reservationId.equals(reservationId))
                .findFirst();
        if (presentReservation.isEmpty()) {
            return announceFailure(new OccupationFailed(parkingSpotId, "no such reservation"));
        }

        Reservation reservation = presentReservation.get();
        Occupied occupied = new Occupied(parkingSpotId, reservation.spotUnits);
        if (reservation.spotUnits.getValue() + currentOccupation() == capacity.getValue()) {
            return announceSuccess(events(parkingSpotId, occupied, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, occupied));
    }

    public Either<ReleasingFailed, ReleasedEvents> release(OccupationId occupationId) {
        Optional<Occupation> presentOccupation = occupations.stream()
                .filter(occupation -> occupation.occupationId.equals(occupationId))
                .findFirst();
        if (presentOccupation.isEmpty()) {
            return announceFailure(new ReleasingFailed(parkingSpotId, "there is no such occupation"));
        }

        Occupation occupation = presentOccupation.get();
        Released released = new Released(parkingSpotId, occupation.spotUnits);
        if (currentOccupation() - occupation.spotUnits.getValue() == 0) {
            return announceSuccess(ReleasedEvents.events(parkingSpotId, released, new CompletelyReleased(parkingSpotId)));
        }
        return announceSuccess(ReleasedEvents.events(parkingSpotId, released));
    }

    private Integer currentOccupation() {
        Integer occupationUnits = occupations.stream()
                .map(Occupation::spotUnits)
                .map(SpotUnits::getValue)
                .reduce(0, Integer::sum);
        Integer reservationUnits = reservations.stream()
                .map(Reservation::spotUnits)
                .map(SpotUnits::getValue)
                .reduce(0, Integer::sum);
        return occupationUnits + reservationUnits;
    }

    private record Occupation(
            @NonNull OccupationId occupationId,
            @NonNull SpotUnits spotUnits
    ) {
    }

    private record Reservation(
            @NonNull ReservationId reservationId,
            @NonNull SpotUnits spotUnits
    ) {
    }

}
