package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupationFailed;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.FullyOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupied;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupiedEvents;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupiedEvents.events;

@Value
public class ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    @NonNull
    ParkingSpotOccupation parkingSpotOccupation;
    @NonNull
    Set<VehicleId> parkedVehicles;

    public Either<ParkingSpotOccupationFailed, ParkingSpotOccupiedEvents> occupy(VehicleId vehicleId, VehicleSize vehicleSize) {
        if (!parkingSpotOccupation.canHandle(vehicleSize)) {
            return announceFailure(new ParkingSpotOccupationFailed(parkingSpotId, vehicleId, "there is not enough space for vehicle"));
        }

        ParkingSpotOccupied parkingSpotOccupied = new ParkingSpotOccupied(parkingSpotId, vehicleId, vehicleSize);
        if (parkingSpotOccupation.occupyWith(vehicleSize).isFull()) {
            return announceSuccess(events(parkingSpotId, parkingSpotOccupied, new FullyOccupied(parkingSpotId)));
        }
        return announceSuccess(events(parkingSpotId, parkingSpotOccupied));
    }

    public Either<ParkingSpotEvent.ParkingSpotLeavingOutFailed, ParkingSpotEvent.ParkingSpotLeftEvents> release(VehicleId vehicleId) {
        if (!parkedVehicles.contains(vehicleId)) {
            return announceFailure(new ParkingSpotEvent.ParkingSpotLeavingOutFailed(parkingSpotId, vehicleId, "vehicle is not parked there"));
        }

        ParkingSpotEvent.ParkingSpotLeft parkingSpotLeft = new ParkingSpotEvent.ParkingSpotLeft(parkingSpotId, vehicleId);
        if (parkedVehicles.stream().allMatch(vehicleId::equals)) {
            return announceSuccess(ParkingSpotEvent.ParkingSpotLeftEvents.events(parkingSpotId, parkingSpotLeft, new ParkingSpotEvent.CompletelyFreedUp(parkingSpotId)));
        }
        return announceSuccess(ParkingSpotEvent.ParkingSpotLeftEvents.events(parkingSpotId, parkingSpotLeft));
    }

}
