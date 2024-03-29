package pl.cezarysanecki.parkingdomain.parking.view.vehicle.infrastructure;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
class VehicleViewEntity {

    UUID vehicleId;
    Option<UUID> parkingSpotId;

}
