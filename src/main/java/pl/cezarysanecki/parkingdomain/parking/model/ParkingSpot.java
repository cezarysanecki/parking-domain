package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.Getter;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents.events;

public class ParkingSpot {

    @Getter
    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final Set<Vehicle> parkedVehicles;
    private final Set<VehicleId> bookedFor;
    private final boolean outOfOrder;

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = Set.of();
        this.bookedFor = Set.of();
        this.outOfOrder = false;
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, Set<Vehicle> parkedVehicles, Set<VehicleId> bookedFor) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = parkedVehicles;
        this.bookedFor = bookedFor;
        this.outOfOrder = false;
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, Set<Vehicle> parkedVehicles, boolean outOfOrder) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = parkedVehicles;
        this.bookedFor = Set.of();
        this.outOfOrder = outOfOrder;
    }

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();

        if (outOfOrder) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "parking on out of order parking spot is forbidden"));
        }
        if (isParked(vehicleId)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "vehicle is already parked on parking spot"));
        }
        if (thereIsNotEnoughSpaceFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "not enough space on parking spot"));
        }
        if (isNotReservedFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "parking spot is not reserved for this vehicle"));
        }

        VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
        if (isReservedFor(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new ReservationFulfilled(parkingSpotId, vehicleId)));
        }
        if (isFullyOccupied(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleParked));
    }

    public Either<ReleasingFailed, VehicleLeft> releaseBy(VehicleId vehicleId) {
        Vehicle foundVehicle = parkedVehicles.stream()
                .filter(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElse(null);
        if (foundVehicle == null) {
            return announceFailure(new ReleasingFailed(parkingSpotId));
        }
        return announceSuccess(new VehicleLeft(parkingSpotId, foundVehicle));
    }

    public List<VehicleLeft> releaseAll() {
        return List.ofAll(parkedVehicles.stream()
                .map(parkedVehicle -> new VehicleLeft(parkingSpotId, parkedVehicle))
                .toList());
    }

    public boolean isEmpty() {
        return parkedVehicles.isEmpty();
    }

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleId)
                .anyMatch(parkedVehicleId -> parkedVehicleId.equals(vehicleId));
    }

    private boolean isNotReservedFor(Vehicle vehicle) {
        return !bookedFor.isEmpty() && !bookedFor.contains(vehicle.getVehicleId());
    }

    private boolean thereIsNotEnoughSpaceFor(Vehicle vehicle) {
        return currentOccupation() + vehicle.getVehicleSizeUnit().getValue() > capacity;
    }

    private boolean isReservedFor(Vehicle vehicle) {
        return !bookedFor.isEmpty() && bookedFor.contains(vehicle.getVehicleId());
    }

    private boolean isFullyOccupied(Vehicle vehicle) {
        return currentOccupation() + vehicle.getVehicleSizeUnit().getValue() == capacity;
    }

    private Integer currentOccupation() {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
    }

}
