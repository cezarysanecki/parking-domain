package pl.cezarysanecki.parkingdomain.clientreservations.model;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.Reservation;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated;
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor
public class ClientReservations {

    private final ClientId clientId;
    private final Set<Reservation> reservations;

    public static ClientReservations empty(ClientId clientId) {
        return new ClientReservations(clientId, Set.of());
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ParkingSpotId parkingSpotId, ReservationSlot reservationSlot) {
        if (hasToManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, reservationSlot, parkingSpotId));
    }

    public Either<ReservationRequestFailed, ReservationRequestCreated> requestReservation(ReservationSlot reservationSlot) {
        if (hasToManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(ReservationRequestCreated.with(clientId, reservationSlot));
    }

    private boolean hasToManyReservations() {
        return !reservations.isEmpty();
    }

}
