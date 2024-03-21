package pl.cezarysanecki.parkingdomain.views.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableParkingSpotView {

    @NonNull
    ParkingSpotId parkingSpotId;
    ParkingSpotType parkingSpotType;
    int leftCapacity;

}
