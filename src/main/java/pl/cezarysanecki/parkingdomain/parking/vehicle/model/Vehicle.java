package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@Value
public class Vehicle {

    @NonNull
    VehicleInformation vehicleInformation;
    @NonNull
    Option<ParkingSpotId> parkedOn;

    public Either<VehicleEvent.VehicleParkingFailed, VehicleEvent.VehicleParked> parkOn(ParkingSpotId parkingSpotId) {
        if (parkedOn.isDefined()) {
            return announceFailure(new VehicleEvent.VehicleParkingFailed(vehicleInformation.getVehicleId(), "vehicle is already parked"));
        }
        return announceSuccess(new VehicleEvent.VehicleParked(vehicleInformation.getVehicleId(), vehicleInformation.getVehicleSize(), parkingSpotId));
    }

    public Either<VehicleEvent.VehicleDrivingAwayFailed, VehicleEvent.VehicleDroveAway> driveAway() {
        if (parkedOn.isEmpty()) {
            return announceFailure(new VehicleEvent.VehicleDrivingAwayFailed(vehicleInformation.getVehicleId(), "vehicle is not parked"));
        }
        return announceSuccess(new VehicleEvent.VehicleDroveAway(vehicleInformation.getVehicleId()));
    }

}
