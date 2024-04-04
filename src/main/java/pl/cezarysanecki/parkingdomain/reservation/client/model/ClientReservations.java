package pl.cezarysanecki.parkingdomain.reservation.client.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted;

import java.util.Set;
import java.util.UUID;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationSubmissionFailed;

@Value
public class ClientReservations {

    ClientId clientId;
    Set<ReservationId> reservations;

    public static ClientReservations empty(ClientId clientId) {
        return new ClientReservations(clientId, Set.of());
    }

    public Either<ReservationSubmissionFailed, ReservationForPartOfParkingSpotSubmitted> createRequest(ParkingSpotId parkingSpotId, VehicleSize vehicleSize) {
        if (willBeTooManyRequests()) {
            return announceFailure(new ReservationSubmissionFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationForPartOfParkingSpotSubmitted(clientId, ReservationId.of(UUID.randomUUID()), parkingSpotId, vehicleSize));
    }

    public Either<ReservationSubmissionFailed, ReservationForWholeParkingSpotSubmitted> createRequest(ParkingSpotId parkingSpotId) {
        if (willBeTooManyRequests()) {
            return announceFailure(new ReservationSubmissionFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationForWholeParkingSpotSubmitted(clientId, ReservationId.of(UUID.randomUUID()), parkingSpotId));
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
