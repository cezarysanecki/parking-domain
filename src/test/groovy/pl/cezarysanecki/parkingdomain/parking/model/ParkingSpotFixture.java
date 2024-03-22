package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static ParkingSpot emptyParkingSpotWith(int capacity) {
        return new ParkingSpot(anyParkingSpotId(), ParkingSpotCapacity.of(capacity), ParkedVehicles.empty(), false);
    }

    public static ParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new ParkingSpot(anyParkingSpotId(), ParkingSpotCapacity.matchFor(vehicle.getVehicleSizeUnit()), new ParkedVehicles(Set.of(vehicle)), false);
    }

    public static ParkingSpot outOfOrderParkingSpot() {
        return new ParkingSpot(anyParkingSpotId(), ParkingSpotCapacity.of(4), ParkedVehicles.empty(), true);
    }

    public static ParkingSpot parkingSpotWith(List<Vehicle> vehicles) {
        Integer capacity = vehicles.stream().map(Vehicle::getVehicleSizeUnit).map(VehicleSizeUnit::getValue).reduce(0, Integer::sum);
        return new ParkingSpot(anyParkingSpotId(), ParkingSpotCapacity.of(capacity), new ParkedVehicles(new HashSet<>(vehicles)), false);
    }

    public static ParkingSpot outOfOrderParkingSpotWith(Vehicle vehicle) {
        return new ParkingSpot(anyParkingSpotId(), ParkingSpotCapacity.of(4), new ParkedVehicles(Set.of(vehicle)), true);
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
