package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParked;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDrivingAwayFailed;
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway;
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParkingFailed;

@Value
public class Vehicle {

    @NonNull
    VehicleInformation vehicleInformation;
    @NonNull
    Option<ParkingSpotId> parkedOn;

    public Either<VehicleParkingFailed, VehicleParked> parkOn(ParkingSpotId parkingSpotId) {
        if (parkedOn.isDefined()) {
            return announceFailure(new VehicleParkingFailed(vehicleInformation.getVehicleId(), "vehicle is already parked"));
        }
        return announceSuccess(new VehicleParked(vehicleInformation.getVehicleId(), vehicleInformation.getVehicleSize(), parkingSpotId));
    }

    public Either<VehicleDrivingAwayFailed, VehicleDroveAway> driveAway() {
        if (parkedOn.isEmpty()) {
            return announceFailure(new VehicleDrivingAwayFailed(vehicleInformation.getVehicleId(), "vehicle is not parked"));
        }
        return announceSuccess(new VehicleDroveAway(vehicleInformation.getVehicleId()));
    }

}
