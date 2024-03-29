package pl.cezarysanecki.parkingdomain.vehicle.parking.application;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleSize;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.Vehicles;

@Slf4j
@RequiredArgsConstructor
public class RegisteringVehicle {

    private final Vehicles vehicles;

    @Value
    public static class Command {

        @NonNull VehicleSize vehicleSize;
    }

    public Try<Result> register(RegisteringVehicle.Command command) {
        return Try.<Result>of(() -> {
            vehicles.publish(new VehicleRegistered(VehicleId.newOne(), command.vehicleSize));
            return new Result.Success();
        }).onFailure(t -> log.error("Failed to register vehicle", t));
    }

    @Value
    public static class VehicleRegistered implements VehicleEvent {

        @NonNull VehicleId vehicleId;
        @NonNull VehicleSize vehicleSize;

    }

}
