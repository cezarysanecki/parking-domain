package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents.events;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents.events;

@RequiredArgsConstructor
public class ParkingSpot {

    @Getter
    private final ParkingSpotId parkingSpotId;
    private final ParkingSpotCapacity capacity;
    private final ParkedVehicles parkedVehicles;
    private final boolean outOfOrder;

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle) {
        if (outOfOrder) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicle.getVehicleId(), "parking on out of order parking spot is forbidden"));
        }
        if (isExceededWith(vehicle)) {
            return announceFailure(new ParkingFailed(parkingSpotId, vehicle.getVehicleId(), ("not enough space on parking spot")));
        }

        VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
        if (isFullyOccupiedWith(vehicle)) {
            return announceSuccess(events(parkingSpotId, vehicleParked, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleParked));
    }

    public Either<ParkingSpotEvent.ReleasingFailed, ParkingSpotEvent.VehicleLeftEvents> driveAway(VehicleId vehicleId) {
        Option<Vehicle> parkedVehicle = parkedVehicles.findBy(vehicleId);
        if (parkedVehicle.isEmpty()) {
            return announceFailure(new ParkingSpotEvent.ReleasingFailed(parkingSpotId, List.of(vehicleId), "vehicle not park on this spot"));
        }
        Vehicle vehicle = parkedVehicle.get();

        ParkingSpotEvent.VehicleLeft vehicleLeft = new ParkingSpotEvent.VehicleLeft(parkingSpotId, vehicle);
        if (isCompletelyFreedUp(vehicle.getVehicleSizeUnit())) {
            return announceSuccess(events(parkingSpotId, vehicleLeft, new CompletelyFreedUp(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, vehicleLeft));
    }

    public boolean isExceededWith(Vehicle vehicle) {
        return parkedVehicles.occupation() + vehicle.getVehicleSizeUnit().getValue() > capacity.getValue();
    }

    public boolean isFullyOccupiedWith(Vehicle vehicle) {
        return parkedVehicles.occupation() + vehicle.getVehicleSizeUnit().getValue() == capacity.getValue();
    }

    public boolean isParked(VehicleId vehicleId) {
        return parkedVehicles.findBy(vehicleId).isDefined();
    }

    public boolean isEmpty() {
        return parkedVehicles.isEmpty();
    }

    public boolean isFull() {
        return parkedVehicles.occupation() == capacity.getValue();
    }

    private boolean isCompletelyFreedUp(VehicleSizeUnit vehicleSizeUnit) {
        return parkedVehicles.occupation() - vehicleSizeUnit.getValue() == 0;
    }

}
