package pl.cezarysanecki.parkingdomain.parking.model.parking;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.cezarysanecki.parkingdomain.commons.policy.Allowance;
import pl.cezarysanecki.parkingdomain.commons.policy.Rejection;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotBasePolicy;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

interface OpenParkingSpotPolicy extends Function2<OpenParkingSpot, Vehicle, Either<Rejection, Allowance>> {

    static List<OpenParkingSpotPolicy> allCurrentPolicies() {
        return ParkingSpotBasePolicy.allCurrentPolicies()
                .map(parkingSpotBasePolicy -> (OpenParkingSpotPolicy) (openParkingSpot, vehicle) ->
                        parkingSpotBasePolicy.apply(openParkingSpot.getBase(), vehicle))
                .toList();
    }

}