package pl.cezarysanecki.parkingdomain.vehicle.model;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class VehicleId {

    UUID value;

    public static VehicleId newOne() {
        return new VehicleId(UUID.randomUUID());
    }

}
