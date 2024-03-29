package pl.cezarysanecki.parkingdomain.vehicle.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleEvent.VehicleDrivingAwayFailed;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleEvent.VehicleDroveAway;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.vehicle.model.VehicleEvent.VehicleParked;
import static pl.cezarysanecki.parkingdomain.vehicle.model.VehicleEvent.VehicleParkingFailed;

class Vehicle {

    @NonNull
    VehicleInformation vehicleInformation;
    @NonNull
    Option<ParkingSpotId> parkedOn;

    public Either<VehicleParkingFailed, VehicleParked> park(ParkingSpotId parkingSpotId) {
        if (parkedOn.isDefined()) {
            return announceFailure(new VehicleParkingFailed(vehicleInformation.getVehicleId(), "vehicle is already parked"));
        }
        return announceSuccess(new VehicleParked(vehicleInformation.getVehicleId(), parkingSpotId));
    }

    public Either<VehicleDrivingAwayFailed, VehicleDroveAway> driveAway() {
        if (parkedOn.isEmpty()) {
            return announceFailure(new VehicleDrivingAwayFailed(vehicleInformation.getVehicleId(), "vehicle is not parked"));
        }
        return announceSuccess(new VehicleDroveAway(vehicleInformation.getVehicleId()));
    }

}
