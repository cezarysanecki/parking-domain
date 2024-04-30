package pl.cezarysanecki.parkingdomain.parking.vehicle.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDrivingAwayFailed;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicles;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway;

@Slf4j
@RequiredArgsConstructor
public class DrivingVehicleAway {

    private final Vehicles vehicles;

    @Value
    public static class Command {

        @NonNull VehicleId vehicleId;

    }

    public Try<Result> driveAway(Command command) {
        VehicleId vehicleId = command.vehicleId;

        return Try.of(() -> {
            Vehicle vehicle = load(vehicleId);
            Either<VehicleDrivingAwayFailed, VehicleDroveAway> result = vehicle.driveAway();
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents));
        }).onFailure(t -> log.error("Failed to occupy parking spot", t));
    }

    private Result publishEvents(VehicleDrivingAwayFailed vehicleDrivingAwayFailed) {
        log.debug("failed to drive vehicle away with id {}, reason: {}", vehicleDrivingAwayFailed.vehicleId(), vehicleDrivingAwayFailed.getReason());
        vehicles.publish(vehicleDrivingAwayFailed);
        return Result.Rejection.with(vehicleDrivingAwayFailed.getReason());
    }

    private Result publishEvents(VehicleDroveAway vehicleDroveAway) {
        log.debug("successfully drive vehicle away with id {}", vehicleDroveAway.vehicleId());
        vehicles.publish(vehicleDroveAway);
        return new Result.Success<>(vehicleDroveAway.vehicleId());
    }

    private Vehicle load(VehicleId vehicleId) {
        return vehicles.findBy(vehicleId)
                .getOrElseThrow(() -> new IllegalStateException("cannot find vehicle with id " + vehicleId));
    }

}
