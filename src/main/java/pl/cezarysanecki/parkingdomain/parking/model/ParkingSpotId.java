package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ParkingSpotId {

    @NonNull
    UUID value;

    @Override
    public String toString() {
        return value.toString();
    }
}
