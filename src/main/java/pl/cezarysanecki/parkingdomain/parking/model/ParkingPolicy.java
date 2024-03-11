package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.Function4;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot.Reservation;

import java.time.Instant;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

interface ParkingPolicy extends Function4<ParkingSpot, Vehicle, Reservation, Instant, Either<Rejection, Allowance>> {

    ParkingPolicy parkingSpotHasEnoughSpace = (ParkingSpot parkingSpot, Vehicle vehicle, Reservation reservation, Instant now) -> {
        if (parkingSpot.hasNotEnoughSpaceFor(vehicle)) {
            return left(Rejection.withReason("parking spot cannot handle another vehicle"));
        }
        return right(new Allowance());
    };

    ParkingPolicy reservationIsNotFulfilled = (ParkingSpot parkingSpot, Vehicle vehicle, Reservation reservation, Instant now) -> {
        if (reservation.isNotFulfilledBy(vehicle, now)) {
            return left(Rejection.withReason("reservation on parking spot is fulfilled by vehicle"));
        }
        return right(new Allowance());
    };

    static List<ParkingPolicy> allCurrentPolicies() {
        return List.of(
                parkingSpotHasEnoughSpace,
                reservationIsNotFulfilled);
    }

}

@Value
class Allowance {
}

@Value
class Rejection {

    @NonNull
    String reason;

    static Rejection withReason(String reason) {
        return new Rejection(reason);
    }

}
