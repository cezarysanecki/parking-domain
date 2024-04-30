package pl.cezarysanecki.parkingdomain.management.vehicle;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class VehicleId {

    UUID value;

    public static VehicleId newOne() {
        return new VehicleId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
