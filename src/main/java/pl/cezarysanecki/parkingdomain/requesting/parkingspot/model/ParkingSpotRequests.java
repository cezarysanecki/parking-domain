package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored;

@Value
public class ParkingSpotRequests {

    ParkingSpotId parkingSpotId;
    ParkingSpotOccupation requestedOccupation;
    Set<RequestId> requests;

    public Either<StoringParkingSpotRequestFailed, RequestForPartOfParkingSpotStored> storeRequest(RequestId requestId, VehicleSize vehicleSize) {
        if (!requestedOccupation.canHandle(vehicleSize)) {
            return announceFailure(new StoringParkingSpotRequestFailed(parkingSpotId, requestId, "not enough parking spot space"));
        }
        return announceSuccess(new RequestForPartOfParkingSpotStored(parkingSpotId, requestId, vehicleSize));
    }

    public Either<StoringParkingSpotRequestFailed, RequestForWholeParkingSpotStored> storeRequest(RequestId requestId) {
        if (!requests.isEmpty()) {
            return announceFailure(new StoringParkingSpotRequestFailed(parkingSpotId, requestId, "there are requests for this parking spot"));
        }
        return announceSuccess(new RequestForWholeParkingSpotStored(parkingSpotId, requestId));
    }

    public Either<ParkingSpotRequestCancellationFailed, ParkingSpotRequestCancelled> cancel(RequestId requestId) {
        if (!requests.contains(requestId)) {
            return announceFailure(new ParkingSpotRequestCancellationFailed(parkingSpotId, requestId, "there is no such request on that parking spot"));
        }
        return announceSuccess(new ParkingSpotRequestCancelled(parkingSpotId, requestId));
    }

    public boolean cannotHandleMore() {
        return requestedOccupation.isFull();
    }

    public boolean isFree() {
        return requests.isEmpty();
    }

}
