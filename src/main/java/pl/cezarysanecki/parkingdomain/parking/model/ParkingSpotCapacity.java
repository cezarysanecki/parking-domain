package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ParkingSpotCapacity {

    int value;

    public ParkingSpotCapacity(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.value = value;
    }

}
