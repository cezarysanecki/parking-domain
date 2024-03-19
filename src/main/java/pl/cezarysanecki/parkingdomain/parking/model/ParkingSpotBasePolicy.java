package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.cezarysanecki.parkingdomain.commons.policy.Allowance;
import pl.cezarysanecki.parkingdomain.commons.policy.Rejection;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public interface ParkingSpotBasePolicy extends Function2<ParkingSpotBase, Vehicle, Either<Rejection, Allowance>> {

    ParkingSpotBasePolicy cannotParkOnOutOfOrderParkingSpotPolicy = (ParkingSpotBase parkingSpot, Vehicle vehicle) -> {
        if (parkingSpot.isOutOfOrder()) {
            return left(Rejection.withReason("parking on out of order parking spot is forbidden"));
        }
        return right(new Allowance());
    };

    ParkingSpotBasePolicy theSameVehicleCannotParkOnParkingSpotPolicy = (ParkingSpotBase parkingSpot, Vehicle vehicle) -> {
        if (parkingSpot.isParked(vehicle.getVehicleId())) {
            return left(Rejection.withReason("vehicle is already parked on parking spot"));
        }
        return right(new Allowance());
    };

    ParkingSpotBasePolicy thereShouldBeEnoughSpaceForVehiclePolicy = (ParkingSpotBase parkingSpot, Vehicle vehicle) -> {
        if (parkingSpot.isExceededWith(vehicle)) {
            return left(Rejection.withReason("not enough space on parking spot"));
        }
        return right(new Allowance());
    };

    static List<ParkingSpotBasePolicy> allCurrentPolicies() {
        return List.of(
                cannotParkOnOutOfOrderParkingSpotPolicy,
                theSameVehicleCannotParkOnParkingSpotPolicy,
                thereShouldBeEnoughSpaceForVehiclePolicy);
    }

}
