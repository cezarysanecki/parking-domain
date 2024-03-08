package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.Set;
import io.vavr.control.Either;
import lombok.Value;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents.events;

@Value
public class ParkingSpot {

    ParkingSpotId parkingSpotId;
    int capacity;
    Set<Vehicle> parkedVehicles;

    public Either<ParkingSpotEvent.ParkingFailed, ParkingSpotEvent.VehicleParkedEvents> park(Vehicle vehicle) {
        if (cannotPark(vehicle)) {
            return announceFailure(new ParkingSpotEvent.ParkingFailed(parkingSpotId));
        }

        ParkingSpotEvent.VehicleParked vehicleParked = new ParkingSpotEvent.VehicleParked(parkingSpotId, vehicle);
        if (isFull()) {
            return announceSuccess(ParkingSpotEvent.VehicleParkedEvents.events(parkingSpotId, vehicleParked, new ParkingSpotEvent.FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(ParkingSpotEvent.VehicleParkedEvents.events(parkingSpotId, vehicleParked));
    }

    public Either<ParkingSpotEvent.ReleasingFailed, ParkingSpotEvent.VehicleLeft> release(VehicleId vehicleId) {
        Vehicle foundVehicle = parkedVehicles
                .find(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                .getOrNull();
        if (foundVehicle == null) {
            return announceFailure(new ParkingSpotEvent.ReleasingFailed(parkingSpotId));
        }
        return announceSuccess(new ParkingSpotEvent.VehicleLeft(parkingSpotId, foundVehicle));
    }

    private boolean cannotPark(Vehicle vehicleSizeUnit) {
        return currentOccupation() + vehicleSizeUnit.getVehicleSizeUnit().getValue() > capacity;
    }

    private boolean isFull() {
        return capacity == currentOccupation();
    }

    private Integer currentOccupation() {
        return parkedVehicles
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(Integer::sum);
    }

}
