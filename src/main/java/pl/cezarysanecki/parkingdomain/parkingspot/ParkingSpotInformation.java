package pl.cezarysanecki.parkingdomain.parkingspot;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class ParkingSpotInformation {

    @NonNull ParkingSpotId parkingSpotId;
    @NonNull ParkingSpotOccupation parkingSpotOccupation;

}
