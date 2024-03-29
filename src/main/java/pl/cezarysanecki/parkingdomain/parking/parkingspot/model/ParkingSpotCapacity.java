package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotCapacity {

    int value;

    public static ParkingSpotCapacity of(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("parking spot capacity must be positive");
        }
        return new ParkingSpotCapacity(value);
    }

}

