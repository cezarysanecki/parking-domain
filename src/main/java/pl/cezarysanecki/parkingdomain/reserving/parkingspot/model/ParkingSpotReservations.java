package pl.cezarysanecki.parkingdomain.reserving.parkingspot.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.WholeParkingSpotReserved;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancellationFailed;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved;

@Value
public class ParkingSpotReservations {

    ParkingSpotId parkingSpotId;
    ParkingSpotOccupation reservedOccupation;
    Set<ReservationId> reservations;

    public Either<ParkingSpotReservationFailed, PartOfParkingSpotReserved> reservePart(ReservationId reservationId, VehicleSize vehicleSize) {
        if (!reservedOccupation.canHandle(vehicleSize)) {
            return announceFailure(new ParkingSpotReservationFailed(parkingSpotId, reservationId, "not to many parking spot space"));
        }
        return announceSuccess(new PartOfParkingSpotReserved(parkingSpotId, reservationId, vehicleSize));
    }

    public Either<ParkingSpotReservationFailed, WholeParkingSpotReserved> reserveWhole(ReservationId reservationId) {
        if (!reservations.isEmpty()) {
            return announceFailure(new ParkingSpotReservationFailed(parkingSpotId, reservationId, "there are reservations for this parking spot"));
        }
        return announceSuccess(new WholeParkingSpotReserved(parkingSpotId, reservationId));
    }

    public Either<ParkingSpotReservationCancellationFailed, ParkingSpotReservationCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ParkingSpotReservationCancellationFailed(parkingSpotId, reservationId, "there is no such reservation on that parking spot"));
        }
        return announceSuccess(new ParkingSpotReservationCancelled(parkingSpotId, reservationId));
    }

    public boolean cannotHandleMore() {
        return reservedOccupation.isFull();
    }

    public boolean isFree() {
        return reservations.isEmpty();
    }

}
