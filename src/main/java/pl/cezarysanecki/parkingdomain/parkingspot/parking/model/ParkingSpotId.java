package pl.cezarysanecki.parkingdomain.parkingspot.parking.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ParkingSpotId {

    int value;

}
