package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleSizeUnit {

    int value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
