package pl.cezarysanecki.parkingdomain.vehicle.parking.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;

public interface VehicleEvent extends DomainEvent {

    VehicleId getVehicleId();

    @Value
    class VehicleParked implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    @Value
    class VehicleParkingFailed implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

    @Value
    class VehicleDroveAway implements VehicleEvent {

        @NonNull VehicleId vehicleId;

    }

    @Value
    class VehicleDrivingAwayFailed implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull String reason;

    }

}
