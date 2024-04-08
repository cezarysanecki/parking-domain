package pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.*;

@AllArgsConstructor
class VehicleEntity {

    UUID vehicleId;
    Option<UUID> parkingSpotId;
    int size;

    VehicleEntity handle(VehicleEvent domainEvent) {
        return API.Match(domainEvent).of(
                API.Case(API.$(instanceOf(VehicleParked.class)), this::handle),
                API.Case(API.$(instanceOf(VehicleDroveAway.class)), this::handle),
                Case($(), () -> this));
    }

    private VehicleEntity handle(VehicleParked domainEvent) {
        parkingSpotId = Option.of(domainEvent.getParkingSpotId().getValue());
        return this;
    }

    private VehicleEntity handle(VehicleDroveAway domainEvent) {
        parkingSpotId = Option.none();
        return this;
    }

}
