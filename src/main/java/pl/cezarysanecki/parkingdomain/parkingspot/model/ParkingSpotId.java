package pl.cezarysanecki.parkingdomain.parkingspot.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ParkingSpotId {

    int value;

}
