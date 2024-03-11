package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import java.util.HashSet;
import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents.events;

@Value
public class ParkingSpot {

    ParkingSpotId parkingSpotId;
    int capacity;
    Set<Vehicle> parkedVehicles;
    Set<VehicleId> bookedFor;

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        this(parkingSpotId, capacity, Set.of(), Set.of());
    }

    public ParkingSpot(
            ParkingSpotId parkingSpotId,
            int capacity,
            Set<Vehicle> parkedVehicles,
            Set<VehicleId> reservations) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>(parkedVehicles);
        this.bookedFor = new HashSet<>(reservations);
    }

    public Either<ReleasingFailed, VehicleLeft> release(VehicleId vehicleId) {
        Vehicle foundVehicle = parkedVehicles.stream()
                .filter(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElse(null);
        if (foundVehicle == null) {
            return announceFailure(new ReleasingFailed(parkingSpotId));
        }
        return announceSuccess(new VehicleLeft(parkingSpotId, foundVehicle));
    }

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();

        if (isNotEnoughSpaceFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId));
        }
        if (isNotFulfillingReservation(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId));
        }

        parkedVehicles.add(vehicle);
        VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
        if (hasReservationFor(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new ReservationFulfilled(parkingSpotId, vehicleId)));
        }
        if (isFullOccupied()) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleParked));
    }

    public boolean isEmpty() {
        return currentOccupation() == 0;
    }

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleId)
                .anyMatch(vehicleId::equals);
    }

    private boolean isNotFulfillingReservation(Vehicle vehicle) {
        return !bookedFor.isEmpty() && !bookedFor.contains(vehicle.getVehicleId());
    }

    private boolean hasReservationFor(Vehicle vehicle) {
        return !bookedFor.isEmpty() && bookedFor.contains(vehicle.getVehicleId());
    }

    private boolean isNotEnoughSpaceFor(Vehicle vehicleSizeUnit) {
        return currentOccupation() + vehicleSizeUnit.getVehicleSizeUnit().getValue() > capacity;
    }

    private boolean isFullOccupied() {
        return capacity == currentOccupation();
    }

    private Integer currentOccupation() {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
    }

}
