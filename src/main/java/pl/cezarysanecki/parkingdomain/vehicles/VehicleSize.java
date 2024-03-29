package pl.cezarysanecki.parkingdomain.vehicles;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VehicleSize {

    int value;

    public static VehicleSize of(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("vehicle size must be positive");
        }
        return new VehicleSize(value);
    }

}
