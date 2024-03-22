package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestCreated;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor
public class ClientReservationRequests {

    @Getter
    private final ClientId clientId;
    private final Set<ReservationId> reservations;
    private final LocalDateTime now;

    public static ClientReservationRequests empty(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> reserve(ReservationType reservationType, ParkingSpotId parkingSpotId) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, parkingSpotId));
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> reserve(ReservationType reservationType, ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId));
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> cancel(ReservationId reservationId) {
        if (doesNotContain(reservationId)) {
            return announceFailure(new ReservationRequestFailed(clientId, "does not have this reservation"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId));
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    private boolean hasTooManyReservations() {
        return reservations.size() >= 1;
    }

    private boolean doesNotContain(ReservationId reservationId) {
        return !reservations.contains(reservationId);
    }

}
