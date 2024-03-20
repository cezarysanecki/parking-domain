package pl.cezarysanecki.parkingdomain.clientreservations.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestApproved;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated;
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestFailed;
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

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ParkingSpotId parkingSpotId, ReservationSlot reservationSlot) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, reservationSlot, parkingSpotId));
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ReservationSlot reservationSlot) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, reservationSlot));
    }

    public Option<ReservationRequestCancelled> cancel(ReservationId reservation) {
        if (reservations.contains(reservation)) {
            return Option.of(new ReservationRequestCancelled(clientId, reservation));
        }
        return Option.none();
    }

    public Option<ReservationRequestApproved> approve(ReservationId reservation) {
        if (reservations.contains(reservation)) {
            return Option.of(new ReservationRequestApproved(clientId, reservation));
        }
        return Option.none();
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    private boolean hasTooManyReservations() {
        return reservations.size() >= 1;
    }

}
