package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupationFailed;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

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
    ParkingSpotInformation parkingSpotInformation;
    @NonNull
    Set<VehicleId> parkedVehicles;

    public Either<ParkingSpotOccupationFailed, ParkingSpotOccupiedEvents> occupy(VehicleId vehicleId, VehicleSize vehicleSize) {
        ParkingSpotOccupation parkingSpotOccupation = parkingSpotInformation.getParkingSpotOccupation();
        if (!parkingSpotOccupation.canHandle(vehicleSize)) {
            return announceFailure(new ParkingSpotOccupationFailed(parkingSpotInformation.getParkingSpotId(), vehicleId, "there is not enough space for vehicle"));
        }

        ParkingSpotOccupied parkingSpotOccupied = new ParkingSpotOccupied(parkingSpotInformation.getParkingSpotId(), vehicleId, vehicleSize);
        if (parkingSpotOccupation.occupyWith(vehicleSize).isFull()) {
            return announceSuccess(events(parkingSpotInformation.getParkingSpotId(), parkingSpotOccupied, new FullyOccupied(parkingSpotInformation.getParkingSpotId())));
        }
        return announceSuccess(events(parkingSpotInformation.getParkingSpotId(), parkingSpotOccupied));
    }

    public Either<ParkingSpotEvent.ParkingSpotLeavingOutFailed, ParkingSpotEvent.ParkingSpotLeftEvents> release(VehicleId vehicleId) {
        if (!parkedVehicles.contains(vehicleId)) {
            return announceFailure(new ParkingSpotEvent.ParkingSpotLeavingOutFailed(parkingSpotInformation.getParkingSpotId(), vehicleId, "vehicle is not parked there"));
        }

        ParkingSpotEvent.ParkingSpotLeft parkingSpotLeft = new ParkingSpotEvent.ParkingSpotLeft(parkingSpotInformation.getParkingSpotId(), vehicleId);
        if (parkedVehicles.stream().allMatch(vehicleId::equals)) {
            return announceSuccess(ParkingSpotEvent.ParkingSpotLeftEvents.events(parkingSpotInformation.getParkingSpotId(), parkingSpotLeft, new ParkingSpotEvent.CompletelyFreedUp(parkingSpotInformation.getParkingSpotId())));
        }
        return announceSuccess(ParkingSpotEvent.ParkingSpotLeftEvents.events(parkingSpotInformation.getParkingSpotId(), parkingSpotLeft));
    }

    public ParkingSpotId getParkingSpotId() {
        return parkingSpotInformation.getParkingSpotId();
    }

}
