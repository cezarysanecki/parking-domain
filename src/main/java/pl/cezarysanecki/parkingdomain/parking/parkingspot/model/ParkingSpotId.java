package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class ParkingSpotId {

    UUID value;

    public static ParkingSpotId newOne() {
        return new ParkingSpotId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}