package pl.cezarysanecki.parkingdomain.parking.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static ParkingSpot emptyParkingSpotWith(int capacity) {
        return new ParkingSpot(anyParkingSpotId(), capacity);
    }

    public static ParkingSpot reservedParkingSpotFor(VehicleId vehicleId) {
        return new ParkingSpot(anyParkingSpotId(), 4, Set.of(), Set.of(vehicleId), Instant.now());
    }

    public static ParkingSpot emptyParkingSpotWith(ParkingSpotId parkingSpotId, int capacity) {
        return new ParkingSpot(parkingSpotId, capacity);
    }

    public static ParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new ParkingSpot(anyParkingSpotId(), vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle));
    }

    public static ParkingSpot parkingSpotWith(ParkingSpotId parkingSpotId, Vehicle vehicle) {
        return new ParkingSpot(parkingSpotId, vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle));
    }

    public static ParkingSpotId anyParkingSpotId() {
        return ParkingSpotId.of(UUID.randomUUID());
    }

    public static Vehicle vehicleWith(int size) {
        return new Vehicle(anyVehicleId(), VehicleSizeUnit.of(size));
    }

    public static Vehicle vehicleWith(VehicleId vehicleId) {
        return new Vehicle(vehicleId, VehicleSizeUnit.of(1));
    }

    public static Vehicle vehicleWith(VehicleId vehicleId, int size) {
        return new Vehicle(vehicleId, VehicleSizeUnit.of(size));
    }

    public static VehicleId anyVehicleId() {
        return VehicleId.of(UUID.randomUUID());
    }

}
