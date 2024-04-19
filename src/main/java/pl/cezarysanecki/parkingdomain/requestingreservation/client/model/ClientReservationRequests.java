package pl.cezarysanecki.parkingdomain.requestingreservation.client.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForWholeParkingSpotRequested;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.RequestingReservationFailed;

@Value
public class ClientReservationRequests {

    ClientId clientId;
    Set<ReservationId> reservations;

    public static ClientReservationRequests empty(ClientId clientId) {
        return new ClientReservationRequests(clientId, Set.of());
    }

    public Either<RequestingReservationFailed, ReservationForPartOfParkingSpotRequested> createRequest(ParkingSpotId parkingSpotId, VehicleSize vehicleSize) {
        if (willBeTooManyRequests()) {
            return announceFailure(new RequestingReservationFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationForPartOfParkingSpotRequested(clientId, ReservationId.newOne(), parkingSpotId, vehicleSize));
    }

    public Either<RequestingReservationFailed, ReservationForWholeParkingSpotRequested> createRequest(ParkingSpotId parkingSpotId) {
        if (willBeTooManyRequests()) {
            return announceFailure(new RequestingReservationFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new ReservationForWholeParkingSpotRequested(clientId, ReservationId.newOne(), parkingSpotId));
    }

    public Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ReservationRequestCancellationFailed(clientId, reservationId, "there is no such reservation request"));
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
