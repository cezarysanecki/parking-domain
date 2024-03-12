package pl.cezarysanecki.parkingdomain.parking.releasing.model;

import pl.cezarysanecki.parkingdomain.GlobalConstants;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static ParkingSpot emptyParkingSpotWith(int capacity) {
        return new ParkingSpot(anyParkingSpotId(), capacity);
    }

    public static ParkingSpot reservedParkingSpotFor(VehicleId vehicleId) {
        return new ParkingSpot(anyParkingSpotId(), GlobalConstants.ParkingSlot.AVAILABLE_SPACE, Set.of(), Set.of(vehicleId));
    }

    public static ParkingSpot emptyParkingSpotWith(ParkingSpotId parkingSpotId, int capacity) {
        return new ParkingSpot(parkingSpotId, capacity);
    }

    public static ParkingSpot outOfOrderParkingSpot() {
        return new ParkingSpot(anyParkingSpotId(), GlobalConstants.ParkingSlot.AVAILABLE_SPACE, Set.of(), true);
    }

    public static ParkingSpot outOfOrderParkingSpotWith(Vehicle vehicle) {
        return new ParkingSpot(anyParkingSpotId(), GlobalConstants.ParkingSlot.AVAILABLE_SPACE, Set.of(vehicle), true);
    }

    public static ParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new ParkingSpot(anyParkingSpotId(), vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle), Set.of());
    }

    public static ParkingSpot parkingSpotWith(List<Vehicle> vehicles) {
        Integer capacity = vehicles.stream().map(Vehicle::getVehicleSizeUnit).map(VehicleSizeUnit::getValue).reduce(0, Integer::sum);
        return new ParkingSpot(anyParkingSpotId(), capacity, new HashSet<>(vehicles), false);
    }

    public static ParkingSpot parkingSpotWith(ParkingSpotId parkingSpotId, Vehicle vehicle) {
        return new ParkingSpot(parkingSpotId, vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle), Set.of());
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
