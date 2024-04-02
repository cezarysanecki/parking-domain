package pl.cezarysanecki.parkingdomain.reservation.parkingspot.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ReservationRequest;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReserved;

@Value
public class ParkingSpotReservations {

    ParkingSpotId parkingSpotId;
    ParkingSpotOccupation reservedOccupation;
    Set<ReservationId> reservations;

    public Either<ParkingSpotReservationFailed, ParkingSpotReserved> reserve(ReservationRequest reservationRequest) {
        if (!reservedOccupation.canHandle(reservationRequest.getVehicleSize())) {
            return announceFailure(new ParkingSpotReservationFailed(parkingSpotId, reservationRequest.getReservationId(), "not to many parking spot space"));
        }
        return announceSuccess(new ParkingSpotReserved(parkingSpotId, reservationRequest.getReservationId()));
    }

    public Either<ParkingSpotReservationCancellationFailed, ParkingSpotReservationCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ParkingSpotReservationCancellationFailed(parkingSpotId, reservationId, "there is no such reservation on that parking spot"));
        }
        return announceSuccess(new ParkingSpotReservationCancelled(parkingSpotId, reservationId));
    }

}
