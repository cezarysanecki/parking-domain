package pl.cezarysanecki.parkingdomain.parking.vehicle.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@RequiredArgsConstructor
public class ParkingVehicle {

    private final Vehicles vehicles;

    @Value
    public static class Command {

        @NonNull VehicleId vehicleId;
        @NonNull ParkingSpotId parkingSpotId;

    }

    public Try<Result> park(Command command) {
        VehicleId vehicleId = command.vehicleId;
        ParkingSpotId parkingSpotId = command.parkingSpotId;

        return Try.of(() -> {
            Vehicle vehicle = load(vehicleId);
            Either<VehicleEvent.VehicleParkingFailed, VehicleEvent.VehicleParked> result = vehicle.parkOn(parkingSpotId);
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to park vehicle", t));
    }

    private Result publishEvents(VehicleEvent.VehicleParkingFailed vehicleParkingFailed) {
        log.debug("failed to park vehicle with id {}, reason: {}", vehicleParkingFailed.getVehicleId(), vehicleParkingFailed.getReason());
        vehicles.publish(vehicleParkingFailed);
        return Result.Rejection.with(vehicleParkingFailed.getReason());
    }

    private Result publishEvents(VehicleEvent.VehicleParked vehicleParked) {
        log.debug("successfully park vehicle with id {} on parking spot with id {}", vehicleParked.getVehicleId(), vehicleParked.getParkingSpotId());
        vehicles.publish(vehicleParked);
        return new Result.Success();
    }

    private Vehicle load(VehicleId vehicleId) {
        return vehicles.findBy(vehicleId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find vehicle with id " + vehicleId));
    }

}
