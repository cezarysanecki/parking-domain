package pl.cezarysanecki.parkingdomain.vehicle.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class VehicleId {

    int value;

}
