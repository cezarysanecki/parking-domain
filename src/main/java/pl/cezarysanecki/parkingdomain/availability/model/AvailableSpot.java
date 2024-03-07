package pl.cezarysanecki.parkingdomain.availability.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

@Value
public class AvailableSpot {

    @NonNull
    ParkingSpotId parkingSpotId;

}
