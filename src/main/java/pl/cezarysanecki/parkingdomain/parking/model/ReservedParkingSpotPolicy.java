package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.cezarysanecki.parkingdomain.commons.policy.Allowance;
import pl.cezarysanecki.parkingdomain.commons.policy.Rejection;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

interface ReservedParkingSpotPolicy extends Function3<ReservedParkingSpot, ReservationId, Vehicle, Either<Rejection, Allowance>> {

    ReservedParkingSpotPolicy cannotParkOnlyOnOwnReservedParkingSpotPolicy = (parkingSpot, reservationId, vehicle) -> {
        if (parkingSpot.isNotReservedFor(reservationId)) {
            return left(Rejection.withReason("parking spot is not reserved for this client"));
        }
        return right(new Allowance());
    };

    static List<ReservedParkingSpotPolicy> allCurrentPolicies() {
        return ParkingSpotBasePolicy.allCurrentPolicies()
                .map(parkingSpotPolicy -> (ReservedParkingSpotPolicy) (parkingSpot, reservationId, vehicle) ->
                        parkingSpotPolicy.apply(parkingSpot.getBase(), vehicle))
                .toList()
                .append(cannotParkOnlyOnOwnReservedParkingSpotPolicy);
    }

}
