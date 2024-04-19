package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.WholeRequestParkingSpotReserved;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.RequestingParkingSpotReservationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.PartRequestOfParkingSpotReserved;

@Value
public class ParkingSpotReservationRequests {

    ParkingSpotId parkingSpotId;
    ParkingSpotOccupation reservedOccupation;
    Set<ReservationId> reservations;

    public Either<RequestingParkingSpotReservationFailed, PartRequestOfParkingSpotReserved> reservePart(ReservationId reservationId, VehicleSize vehicleSize) {
        if (!reservedOccupation.canHandle(vehicleSize)) {
            return announceFailure(new RequestingParkingSpotReservationFailed(parkingSpotId, reservationId, "not to many parking spot space"));
        }
        return announceSuccess(new PartRequestOfParkingSpotReserved(parkingSpotId, reservationId, vehicleSize));
    }

    public Either<RequestingParkingSpotReservationFailed, WholeRequestParkingSpotReserved> reserveWhole(ReservationId reservationId) {
        if (!reservations.isEmpty()) {
            return announceFailure(new RequestingParkingSpotReservationFailed(parkingSpotId, reservationId, "there are reservations for this parking spot"));
        }
        return announceSuccess(new WholeRequestParkingSpotReserved(parkingSpotId, reservationId));
    }

    public Either<ParkingSpotReservationRequestCancellationFailed, ParkingSpotReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ParkingSpotReservationRequestCancellationFailed(parkingSpotId, reservationId, "there is no such reservation on that parking spot"));
        }
        return announceSuccess(new ParkingSpotReservationRequestCancelled(parkingSpotId, reservationId));
    }

    public boolean cannotHandleMore() {
        return reservedOccupation.isFull();
    }

    public boolean isFree() {
        return reservations.isEmpty();
    }

}
