package pl.cezarysanecki.parkingdomain.vehicle.parking.infrastructure;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent.VehicleParked;

import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent.VehicleDroveAway;

@AllArgsConstructor
class VehicleEntity {

    UUID vehicleId;
    Option<UUID> parkingSpotId;
    int size;

    VehicleEntity handle(VehicleEvent domainEvent) {
        return API.Match(domainEvent).of(
                Case($(instanceOf(VehicleParked.class)), this::handle),
                Case($(instanceOf(VehicleDroveAway.class)), this::handle),
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
