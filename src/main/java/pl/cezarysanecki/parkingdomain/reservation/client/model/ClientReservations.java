package pl.cezarysanecki.parkingdomain.reservation.client.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

import java.util.Set;
import java.util.UUID;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestSubmissionFailed;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestSubmitted;

@Value
public class ClientReservations {

    ClientId clientId;
    Set<ReservationId> reservations;

    public static ClientReservations empty(ClientId clientId) {
        return new ClientReservations(clientId, Set.of());
    }

    public Either<ReservationRequestSubmissionFailed, ReservationRequestSubmitted> createRequest(ParkingSpotId parkingSpotId, VehicleSize vehicleSize) {
        if (willBeTooManyRequests()) {
            return announceFailure(new ReservationRequestSubmissionFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationRequestSubmitted(new ReservationRequest(clientId, ReservationId.of(UUID.randomUUID()), parkingSpotId, vehicleSize)));
    }

    public Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ReservationRequestCancellationFailed(clientId, reservationId, "there is no such reservation"));
        }
        return announceSuccess(new ReservationRequestCancelled(clientId, reservationId));
    }

    private boolean willBeTooManyRequests() {
        return reservations.size() + 1 > 1;
    }

}
