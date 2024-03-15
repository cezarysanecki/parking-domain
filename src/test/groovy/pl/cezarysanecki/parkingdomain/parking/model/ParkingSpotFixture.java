package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static CommonParkingSpot emptyParkingSpotWith(int capacity) {
        return new CommonParkingSpot(anyParkingSpotId(), capacity);
    }

    public static CommonParkingSpot reservedParkingSpotFor(VehicleId vehicleId) {
        return new CommonParkingSpot(anyParkingSpotId(), 4, Set.of(), Set.of(vehicleId));
    }

    public static CommonParkingSpot emptyParkingSpotWith(ParkingSpotId parkingSpotId, int capacity) {
        return new CommonParkingSpot(parkingSpotId, capacity);
    }

    public static CommonParkingSpot outOfOrderParkingSpot() {
        return new CommonParkingSpot(anyParkingSpotId(), 4, Set.of(), true);
    }

    public static CommonParkingSpot outOfOrderParkingSpotWith(Vehicle vehicle) {
        return new CommonParkingSpot(anyParkingSpotId(), 4, Set.of(vehicle), true);
    }

    public static CommonParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new CommonParkingSpot(anyParkingSpotId(), vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle), Set.of());
    }

    public static CommonParkingSpot parkingSpotWith(List<Vehicle> vehicles) {
        Integer capacity = vehicles.stream().map(Vehicle::getVehicleSizeUnit).map(VehicleSizeUnit::getValue).reduce(0, Integer::sum);
        return new CommonParkingSpot(anyParkingSpotId(), capacity, new HashSet<>(vehicles), false);
    }

    public static CommonParkingSpot parkingSpotWith(ParkingSpotId parkingSpotId, Vehicle vehicle) {
        return new CommonParkingSpot(parkingSpotId, vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle), Set.of());
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
