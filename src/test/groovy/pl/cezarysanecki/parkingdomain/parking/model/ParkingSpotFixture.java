package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static NormalParkingSpot emptyParkingSpotWith(int capacity) {
        return new NormalParkingSpot(anyParkingSpotId(), capacity);
    }

    public static NormalParkingSpot emptyParkingSpotWith(ParkingSpotId parkingSpotId, int capacity) {
        return new NormalParkingSpot(parkingSpotId, capacity);
    }

    public static NormalParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new NormalParkingSpot(anyParkingSpotId(), vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle));
    }

    public static NormalParkingSpot parkingSpotWith(ParkingSpotId parkingSpotId, Vehicle vehicle) {
        return new NormalParkingSpot(parkingSpotId, vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle));
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
