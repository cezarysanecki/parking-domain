package pl.cezarysanecki.parkingdomain.management.vehicle;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RegisteringVehicle {

    private final CatalogueVehicleDatabase database;
    private final EventPublisher eventPublisher;

    public Try<Result> register(UUID clientId, int size, String make, String model) {
        return Try.<Result>of(() -> {
            Vehicle vehicle = new Vehicle(UUID.randomUUID(), size, make, model, clientId);
            log.debug("registering vehicle with id {}", vehicle.getVehicleId());

            database.saveNew(vehicle);

            eventPublisher.publish(new VehicleRegistered(vehicle));

            return new Result.Success<>(vehicle.getVehicleId());
        }).onFailure(t -> log.error("failed to register vehicle", t));
    }

}
