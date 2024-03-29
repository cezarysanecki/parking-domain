package pl.cezarysanecki.parkingdomain.parkingspot.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parkingspot.model.ParkingSpotEvent.CompletelyFreedUp;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleId;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parkingspot.model.ParkingSpotEvent.ParkingSpotLeavingOutFailed;
import static pl.cezarysanecki.parkingdomain.parkingspot.model.ParkingSpotEvent.ParkingSpotLeft;
import static pl.cezarysanecki.parkingdomain.parkingspot.model.ParkingSpotEvent.ParkingSpotLeftEvents;
import static pl.cezarysanecki.parkingdomain.parkingspot.model.ParkingSpotEvent.ParkingSpotLeftEvents.events;

@Value
public class OccupiedParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotInformation parkingSpotInformation;
    @NonNull
    Set<VehicleId> parkedVehicles;

    public Either<ParkingSpotLeavingOutFailed, ParkingSpotLeftEvents> driveAway(VehicleId vehicleId) {
        if (!parkedVehicles.contains(vehicleId)) {
            return announceFailure(new ParkingSpotLeavingOutFailed(getParkingSpotId(), vehicleId));
        }

        ParkingSpotLeft parkingSpotLeft = new ParkingSpotLeft(getParkingSpotId(), vehicleId);
        if (parkedVehicles.stream().allMatch(vehicleId::equals)) {
            return announceSuccess(events(getParkingSpotId(), parkingSpotLeft, new CompletelyFreedUp(getParkingSpotId())));
        }
        return announceSuccess(events(getParkingSpotId(), parkingSpotLeft));
    }

}
