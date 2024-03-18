package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.policy.Rejection;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.FullyOccupied;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParkedEvents;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OpenParkingSpot implements ParkingSpot {

    @Getter
    private final ParkingSpotBase parkingSpot;

    private final List<OpenParkingSpotPolicy> parkingPolicies;

    public Either<ParkingFailed, VehicleParkedEvents> park(Vehicle vehicle) {
        ParkingSpotId parkingSpotId = parkingSpot.getParkingSpotId();

        Option<Rejection> rejection = vehicleCanPark(vehicle);
        if (rejection.isEmpty()) {
            VehicleParked vehicleParked = new VehicleParked(parkingSpotId, vehicle);
            if (parkingSpot.isFullyOccupiedWith(vehicle)) {
                return announceSuccess(new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.of(new FullyOccupied(parkingSpotId)), Option.none()));
            }
            return announceSuccess(new VehicleParkedEvents(parkingSpotId, vehicleParked, Option.none(), Option.none()));
        }
        return announceFailure(new ParkingFailed(parkingSpotId, vehicle.getVehicleId(), rejection.get().getReason().getReason()));
    }

    private Option<Rejection> vehicleCanPark(Vehicle vehicle) {
        return parkingPolicies
                .toStream()
                .map(policy -> policy.apply(this, vehicle))
                .find(Either::isLeft)
                .map(Either::getLeft);
    }

}
