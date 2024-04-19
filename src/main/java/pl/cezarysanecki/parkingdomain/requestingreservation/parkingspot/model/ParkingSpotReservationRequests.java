package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForWholeParkingSpotStored;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.StoringParkingSpotReservationRequestFailed;

@Value
public class ParkingSpotReservationRequests {

    ParkingSpotId parkingSpotId;
    ParkingSpotOccupation reservedOccupation;
    Set<ReservationId> reservations;

    public Either<StoringParkingSpotReservationRequestFailed, ReservationRequestForPartOfParkingSpotStored> storeForPart(ReservationId reservationId, VehicleSize vehicleSize) {
        if (!reservedOccupation.canHandle(vehicleSize)) {
            return announceFailure(new StoringParkingSpotReservationRequestFailed(parkingSpotId, reservationId, "not enough parking spot space"));
        }
        return announceSuccess(new ReservationRequestForPartOfParkingSpotStored(parkingSpotId, reservationId, vehicleSize));
    }

    public Either<StoringParkingSpotReservationRequestFailed, ReservationRequestForWholeParkingSpotStored> storeForWhole(ReservationId reservationId) {
        if (!reservations.isEmpty()) {
            return announceFailure(new StoringParkingSpotReservationRequestFailed(parkingSpotId, reservationId, "there are reservation requests for this parking spot"));
        }
        return announceSuccess(new ReservationRequestForWholeParkingSpotStored(parkingSpotId, reservationId));
    }

    public Either<ParkingSpotReservationRequestCancellationFailed, ParkingSpotReservationRequestCancelled> cancel(ReservationId reservationId) {
        if (!reservations.contains(reservationId)) {
            return announceFailure(new ParkingSpotReservationRequestCancellationFailed(parkingSpotId, reservationId, "there is no such reservation request on that parking spot"));
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
