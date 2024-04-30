package pl.cezarysanecki.parkingdomain.requesting.parkingspot.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId;

public interface ParkingSpotRequestEvent extends DomainEvent {

    ParkingSpotId getParkingSpotId();

    @Value
    class RequestForPartOfParkingSpotStored implements ParkingSpotRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull RequestId requestId;
        @NonNull VehicleSize vehicleSize;

    }

    @Value
    class RequestForWholeParkingSpotStored implements ParkingSpotRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull RequestId requestId;

    }

    @Value
    class StoringParkingSpotRequestFailed implements ParkingSpotRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull RequestId requestId;
        @NonNull String reason;

    }

    @Value
    class ParkingSpotRequestCancelled implements ParkingSpotRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull RequestId requestId;

    }

    @Value
    class ParkingSpotRequestCancellationFailed implements ParkingSpotRequestEvent {

        @NonNull ParkingSpotId parkingSpotId;
        @NonNull RequestId requestId;
        @NonNull String reason;

    }

}
