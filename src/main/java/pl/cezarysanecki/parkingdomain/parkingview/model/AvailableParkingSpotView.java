package pl.cezarysanecki.parkingdomain.parkingview.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

@Value
public class AvailableParkingSpotView {

    @NonNull
    ParkingSpotId parkingSpotId;
    int leftCapacity;

}
