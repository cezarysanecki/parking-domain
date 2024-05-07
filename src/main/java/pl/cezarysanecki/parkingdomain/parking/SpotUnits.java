package pl.cezarysanecki.parkingdomain.parking;

import lombok.Value;

@Value(staticConstructor = "of")
public class SpotUnits {

    int value;

    public SpotUnits(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("spot units cannot be negative");
        }
        this.value = value;
    }

}
