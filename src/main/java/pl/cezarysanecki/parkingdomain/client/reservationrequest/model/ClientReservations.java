package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestCreated;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor
public class ClientReservations {

    @Getter
    private final ClientId clientId;
    private final Set<ReservationId> reservations;
    private final LocalDateTime now;

    public static ClientReservations empty(ClientId clientId, LocalDateTime now) {
        return new ClientReservations(clientId, Set.of(), now);
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ParkingSpotId parkingSpotId) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, parkingSpotId));
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation() {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId));
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    private boolean hasTooManyReservations() {
        return reservations.size() >= 1;
    }

}
