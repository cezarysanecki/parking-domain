package pl.cezarysanecki.parkingdomain.reserving.client.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationSubmissionFailed;

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
        return announceSuccess(new ReservationForPartOfParkingSpotSubmitted(clientId, ReservationId.newOne(), parkingSpotId, vehicleSize));
    }

    public Either<ReservationSubmissionFailed, ReservationForWholeParkingSpotSubmitted> createRequest(ParkingSpotId parkingSpotId) {
        if (willBeTooManyRequests()) {
            return announceFailure(new ReservationSubmissionFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationForWholeParkingSpotSubmitted(clientId, ReservationId.newOne(), parkingSpotId));
    }

    public Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ReservationRequestCancellationFailed(clientId, reservationId, "there is no such reservation"));
        }
        return announceSuccess(new ReservationRequestCancelled(clientId, reservationId));
    }

    public boolean contains(ReservationId reservationId) {
        return reservations.contains(reservationId);
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    private boolean willBeTooManyRequests() {
        return reservations.size() + 1 > 1;
    }

}
