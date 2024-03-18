package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static ParkingSpotBase emptyParkingSpotWith(int capacity) {
        return new ParkingSpotBase(anyParkingSpotId(), ParkingSpotCapacity.of(capacity), ParkedVehicles.empty(), false);
    }

    public static ParkingSpotBase parkingSpotWith(Vehicle vehicle) {
        return new ParkingSpotBase(anyParkingSpotId(), ParkingSpotCapacity.matchFor(vehicle.getVehicleSizeUnit()), new ParkedVehicles(Set.of(vehicle)), false);
    }

    public static ParkingSpotBase outOfOrderParkingSpot() {
        return new ParkingSpotBase(anyParkingSpotId(), ParkingSpotCapacity.of(4), ParkedVehicles.empty(), true);
    }

    public static ParkingSpotBase parkingSpotWith(List<Vehicle> vehicles) {
        Integer capacity = vehicles.stream().map(Vehicle::getVehicleSizeUnit).map(VehicleSizeUnit::getValue).reduce(0, Integer::sum);
        return new ParkingSpotBase(anyParkingSpotId(), ParkingSpotCapacity.of(capacity), new ParkedVehicles(new HashSet<>(vehicles)), false);
    }

    public static ParkingSpotBase outOfOrderParkingSpotWith(Vehicle vehicle) {
        return new ParkingSpotBase(anyParkingSpotId(), ParkingSpotCapacity.of(4), new ParkedVehicles(Set.of(vehicle)), true);
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
