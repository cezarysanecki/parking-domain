package pl.cezarysanecki.parkingdomain.parkingview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableParkingSpotView {

    @NonNull
    ParkingSpotId parkingSpotId;
    int leftCapacity;

}
