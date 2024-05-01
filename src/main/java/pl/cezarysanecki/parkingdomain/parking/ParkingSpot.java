package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.vehicle.SpotUnits;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotOccupation;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.*;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.FullyOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupiedEvents.events;

@Value
public class ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    @NonNull
    SpotOccupation spotOccupation;
    @NonNull
    Set<VehicleId> parkedVehicles;

    public Either<ParkingSpotOccupationFailed, ParkingSpotOccupiedEvents> occupy(VehicleId vehicleId, SpotUnits spotUnits) {
        if (spotOccupation.cannotHandle(spotUnits)) {
            return announceFailure(new ParkingSpotOccupationFailed(parkingSpotId, vehicleId, "there is not enough space for vehicle"));
        }

        ParkingSpotOccupied parkingSpotOccupied = new ParkingSpotOccupied(parkingSpotId, vehicleId, spotUnits);
        if (spotOccupation.occupyWith(spotUnits).isFull()) {
            return announceSuccess(events(parkingSpotId, parkingSpotOccupied, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, parkingSpotOccupied));
    }

    public Either<ParkingSpotLeavingOutFailed, ParkingSpotLeftEvents> release(VehicleId vehicleId) {
        if (!parkedVehicles.contains(vehicleId)) {
            return announceFailure(new ParkingSpotLeavingOutFailed(parkingSpotId, vehicleId, "vehicle is not parked there"));
        }

        ParkingSpotLeft parkingSpotLeft = new ParkingSpotLeft(parkingSpotId, vehicleId);
        if (parkedVehicles.stream().allMatch(vehicleId::equals)) {
            return announceSuccess(ParkingSpotLeftEvents.events(parkingSpotId, parkingSpotLeft, new CompletelyFreedUp(parkingSpotId)));
        }
        return announceSuccess(ParkingSpotLeftEvents.events(parkingSpotId, parkingSpotLeft));
    }

}
