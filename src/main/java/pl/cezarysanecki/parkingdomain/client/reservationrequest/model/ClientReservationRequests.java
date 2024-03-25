package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;

import java.time.LocalDateTime;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.CancellationOfReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestFailed;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ClientReservationRequests {

    @Getter
    private final ClientId clientId;
    private final Set<ReservationId> reservations;
    private final LocalDateTime now;

    public static ClientReservationRequests empty(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public Either<ReservationRequestFailed, ChosenParkingSpotReservationRequested> reserve(ReservationPeriod reservationPeriod, ParkingSpotId parkingSpotId) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(new ChosenParkingSpotReservationRequested(clientId, reservationPeriod, parkingSpotId));
    }

    public Either<ReservationRequestFailed, AnyParkingSpotReservationRequested> reserve(ReservationPeriod reservationPeriod, ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
        if (hasTooManyReservations()) {
            return announceFailure(new ReservationRequestFailed(clientId, "cannot have more reservations"));
        }
        return announceSuccess(new AnyParkingSpotReservationRequested(clientId, reservationPeriod, parkingSpotType, vehicleSizeUnit));
    }

    public Either<CancellationOfReservationRequestFailed, ReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (doesNotContain(reservationId)) {
            return announceFailure(new CancellationOfReservationRequestFailed(clientId, reservationId, "does not have this reservation"));
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
