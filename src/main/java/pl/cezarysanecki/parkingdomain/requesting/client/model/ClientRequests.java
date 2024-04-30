package pl.cezarysanecki.parkingdomain.requesting.client.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.MakingRequestFailed;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancellationFailed;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;

@Value
public class ClientRequests {

    ClientId clientId;
    Set<RequestId> requests;

    public static ClientRequests empty(ClientId clientId) {
        return new ClientRequests(clientId, Set.of());
    }

    public Either<MakingRequestFailed, RequestForPartOfParkingSpotMade> createRequest(ParkingSpotId parkingSpotId, VehicleSize vehicleSize) {
        if (willBeTooManyRequests()) {
            return announceFailure(new MakingRequestFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new RequestForPartOfParkingSpotMade(clientId, RequestId.newOne(), parkingSpotId, vehicleSize));
    }

    public Either<MakingRequestFailed, RequestForWholeParkingSpotMade> createRequest(ParkingSpotId parkingSpotId) {
        if (willBeTooManyRequests()) {
            return announceFailure(new MakingRequestFailed(clientId, "client has too many requests"));
        }
        return announceSuccess(new RequestForWholeParkingSpotMade(clientId, RequestId.newOne(), parkingSpotId));
    }

    public Either<RequestCancellationFailed, RequestCancelled> cancel(RequestId requestId) {
        if (!requests.contains(requestId)) {
            return announceFailure(new RequestCancellationFailed(clientId, requestId, "there is no such request"));
        }
        return announceSuccess(new RequestCancelled(clientId, requestId));
    }

    public boolean contains(RequestId requestId) {
        return requests.contains(requestId);
    }

    public boolean isEmpty() {
        return requests.isEmpty();
    }

    private boolean willBeTooManyRequests() {
        return requests.size() + 1 > 1;
    }

}
