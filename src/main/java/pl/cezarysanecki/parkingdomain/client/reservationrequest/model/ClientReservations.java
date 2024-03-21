package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestApproved;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot;

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

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ParkingSpotId parkingSpotId, ReservationSlot reservationSlot) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        if (isTooSoon(reservationSlot)) {
            return announceFailure(new ReservationRequestFailed(clientId, "reservation is too soon from now"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, reservationSlot, parkingSpotId));
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ReservationSlot reservationSlot) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        if (isTooSoon(reservationSlot)) {
            return announceFailure(new ReservationRequestFailed(clientId, "reservation is too soon from now"));
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

    private boolean isTooSoon(ReservationSlot reservationSlot) {
        return now.plusHours(3).isAfter(reservationSlot.getSince());
    }

}
