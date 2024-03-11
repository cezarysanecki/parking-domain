package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents.events;

@RequiredArgsConstructor
public class ParkingSpot {

    @Getter
    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final Set<Vehicle> parkedVehicles;
    private final Set<VehicleId> bookedFor;

    public ParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = Set.of();
        this.bookedFor = Set.of();
    }

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();

        if (isNotReservedFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "parking spot is not reserved for this vehicle"));
        }
        if (thereIsNotEnoughSpaceFor(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicleId, "not enough space on parking spot"));
        }

        ParkingSpotEvent.VehicleParked vehicleParked = new ParkingSpotEvent.VehicleParked(parkingSpotId, vehicle);
        if (isReservedFor(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new ReservationFulfilled(parkingSpotId, vehicleId)));
        }
        if (isFullyOccupied(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleParked));
    }

    public Either<ParkingSpotEvent.ReleasingFailed, ParkingSpotEvent.VehicleLeft> releaseBy(VehicleId vehicleId) {
        Vehicle foundVehicle = parkedVehicles.stream()
                .filter(parkedVehicle -> parkedVehicle.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElse(null);
        if (foundVehicle == null) {
            return announceFailure(new ParkingSpotEvent.ReleasingFailed(parkingSpotId));
        }
        return announceSuccess(new ParkingSpotEvent.VehicleLeft(parkingSpotId, foundVehicle));
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
