package pl.cezarysanecki.parkingdomain.management.vehicle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotUnits {

    int value;

    public static SpotUnits of(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("vehicle size must be positive");
        }
        return new SpotUnits(value);
    }

}
