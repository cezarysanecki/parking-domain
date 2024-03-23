package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.CancellationOfReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ClientReservationRequests {

    @Getter
    private final ClientId clientId;
    private final Set<ClientReservationRequestId> clientReservationRequests;
    private final LocalDateTime now;

    public static ClientReservationRequests empty(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> reserve(ReservationType reservationType, ParkingSpotId parkingSpotId) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(new ChosenParkingSpotReservationRequested(clientId, reservationType, parkingSpotId));
    }

    public Either<ReservationRequestFailed, AnyParkingSpotReservationRequested> reserve(ReservationType reservationType, ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(new AnyParkingSpotReservationRequested(clientId, reservationType, parkingSpotType, vehicleSizeUnit));
    }

    public Either<CancellationOfReservationRequestFailed, ReservationRequestCancelled> cancel(ClientReservationRequestId clientReservationRequestId) {
        if (doesNotContain(clientReservationRequestId)) {
            return announceFailure(new CancellationOfReservationRequestFailed(clientId, clientReservationRequestId, "does not have this reservation"));
        }
        return announceSuccess(new ReservationRequestCancelled(clientId, clientReservationRequestId));
    }

    public boolean isEmpty() {
        return clientReservationRequests.isEmpty();
    }

    private boolean hasTooManyReservations() {
        return clientReservationRequests.size() >= 1;
    }

    private boolean doesNotContain(ClientReservationRequestId clientReservationRequestId) {
        return !clientReservationRequests.contains(clientReservationRequestId);
    }

}
