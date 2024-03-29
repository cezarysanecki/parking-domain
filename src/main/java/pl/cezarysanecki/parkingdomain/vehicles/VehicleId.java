package pl.cezarysanecki.parkingdomain.vehicles;

import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleId {

    int value;

}
