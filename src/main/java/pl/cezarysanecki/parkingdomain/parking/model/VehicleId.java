package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class VehicleId {

    @NonNull
    UUID value;

    @Override
    public String toString() {
        return value.toString();
    }

}
