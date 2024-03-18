package pl.cezarysanecki.parkingdomain.parking.model;

import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ParkingSpotFixture {

    public static OpenParkingSpot emptyParkingSpotWith(int capacity) {
        return new OpenParkingSpot(anyParkingSpotId(), capacity);
    }

    public static OpenParkingSpot reservedParkingSpotFor(ReservationId reservationId) {
        return new OpenParkingSpot(anyParkingSpotId(), 4, reservationId);
    }

    public static OpenParkingSpot emptyParkingSpotWith(ParkingSpotId parkingSpotId, int capacity) {
        return new OpenParkingSpot(parkingSpotId, capacity);
    }

    public static OpenParkingSpot outOfOrderParkingSpot() {
        return new OpenParkingSpot(anyParkingSpotId(), 4, Set.of(), true);
    }

    public static OpenParkingSpot outOfOrderParkingSpotWith(Vehicle vehicle) {
        return new OpenParkingSpot(anyParkingSpotId(), 4, Set.of(vehicle), true);
    }

    public static OpenParkingSpot parkingSpotWith(Vehicle vehicle) {
        return new OpenParkingSpot(anyParkingSpotId(), vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle), false);
    }

    public static OpenParkingSpot parkingSpotWith(List<Vehicle> vehicles) {
        Integer capacity = vehicles.stream().map(Vehicle::getVehicleSizeUnit).map(VehicleSizeUnit::getValue).reduce(0, Integer::sum);
        return new OpenParkingSpot(anyParkingSpotId(), capacity, new HashSet<>(vehicles), false);
    }

    public static OpenParkingSpot parkingSpotWith(ParkingSpotId parkingSpotId, Vehicle vehicle) {
        return new OpenParkingSpot(parkingSpotId, vehicle.getVehicleSizeUnit().getValue(), Set.of(vehicle), false);
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
