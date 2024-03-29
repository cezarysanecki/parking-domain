package pl.cezarysanecki.parkingdomain.vehicle.view.infrastructure;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
class VehicleViewEntity {

    UUID vehicleId;
    Option<UUID> parkingSpotId;

}
