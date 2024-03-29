package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.util.Set;

import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.AnyParkingSpotReservationRequested;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestCancellationFailed;

import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ChosenParkingSpotReservationRequested;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestCancelled;

import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor
public class ClientReservationRequests {

    @Getter
    private final ClientId clientId;
    private final Set<ReservationId> reservations;

    public static ClientReservationRequests empty(ClientId clientId) {
        return new ClientReservationRequests(clientId, Set.of());
    }

    public Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> createRequest(ReservationPeriod reservationPeriod, ParkingSpotId parkingSpotId) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(new ChosenParkingSpotReservationRequested(clientId, reservationPeriod, parkingSpotId));
    }

    public Either<ReservationRequestFailed, AnyParkingSpotReservationRequested> createRequest(ReservationPeriod reservationPeriod, ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(new AnyParkingSpotReservationRequested(clientId, reservationPeriod, parkingSpotType, vehicleSizeUnit));
    }

    public Either<ReservationRequestCancellationFailed, ReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (doesNotContain(reservationId)) {
            return announceFailure(new ReservationRequestCancellationFailed(clientId, reservationId, "does not have this reservation"));
        }
        return announceSuccess(new ReservationRequestCancelled(clientId, reservationId));
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
