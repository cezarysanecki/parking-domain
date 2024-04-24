package pl.cezarysanecki.parkingdomain.requesting.client.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

public interface ClientRequestsEvent extends DomainEvent {

    ClientId getClientId();

    @Value
    class RequestForPartOfParkingSpotMade implements ClientRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull RequestId requestId;
        @NonNull ParkingSpotId parkingSpotId;
        @NonNull VehicleSize vehicleSize;

    }

    @Value
    class RequestForWholeParkingSpotMade implements ClientRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull RequestId requestId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class MakingRequestFailed implements ClientRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull String reason;

    }

    @Value
    class RequestCancelled implements ClientRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull RequestId requestId;

    }

    @Value
    class RequestCancellationFailed implements ClientRequestsEvent {

        @NonNull ClientId clientId;
        @NonNull RequestId requestId;
        @NonNull String reason;

    }

}
