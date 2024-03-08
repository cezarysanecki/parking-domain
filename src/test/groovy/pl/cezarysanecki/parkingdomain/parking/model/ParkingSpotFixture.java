package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static ParkingSpot emptyWithCapacity(int capacity) {
        return new ParkingSpot(anyParkingSpotId(), capacity);
    }

    public static ParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new ParkingSpot(anyParkingSpotId(), vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle));
    }

    public static ParkingSpotId anyParkingSpotId() {
        return ParkingSpotId.of(UUID.randomUUID());
    }

    public static Vehicle vehicleWithSize(int size) {
        return new Vehicle(anyVehicleId(), VehicleSizeUnit.of(size));
    }

    public static VehicleId anyVehicleId() {
        return VehicleId.of(UUID.randomUUID());
    }

}
