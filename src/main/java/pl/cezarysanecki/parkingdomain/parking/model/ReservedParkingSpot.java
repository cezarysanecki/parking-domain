package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReservationFulfilled;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@Value
public class ReservedParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    @NonNull
    Set<VehicleId> bookedFor;
    boolean parked;

    public Either<ParkingSpotEvent.ParkingFailed, ReservationFulfilled> park(Vehicle vehicle) {
        VehicleId vehicleId = vehicle.getVehicleId();

        if (!this.bookedFor.contains(vehicleId)) {
            return announceFailure(new ParkingSpotEvent.ParkingFailed(parkingSpotId, vehicleId));
        }
        return announceSuccess(new ReservationFulfilled(parkingSpotId, vehicleId));
    }

}
