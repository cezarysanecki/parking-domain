package pl.cezarysanecki.parkingdomain.parking;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class ParkingSpotId {

    @NonNull
    Long id;

}
