package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Either;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
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

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        this(parkingSpotId, capacity, Set.of());
    }

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity, Set<Vehicle> parkedVehicles) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>(parkedVehicles);
    }

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle) {
        if (cannotPark(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId));
        }

        parkedVehicles.add(vehicle);
        VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
        if (isFull()) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleParked));
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

    private boolean cannotPark(Vehicle vehicleSizeUnit) {
        return currentOccupation() + vehicleSizeUnit.getVehicleSizeUnit().getValue() > capacity;
    }

    private boolean isFull() {
        return capacity == currentOccupation();
    }

    private Integer currentOccupation() {
        return parkedVehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
    }

}
