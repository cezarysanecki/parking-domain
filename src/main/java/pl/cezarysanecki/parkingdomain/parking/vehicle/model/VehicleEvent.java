package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;

public interface VehicleEvent extends DomainEvent {

    VehicleId getVehicleId();

    @Value
    class VehicleParked implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull VehicleSize vehicleSize;
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
