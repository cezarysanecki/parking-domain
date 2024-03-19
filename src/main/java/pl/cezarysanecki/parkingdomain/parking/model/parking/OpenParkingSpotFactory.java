package pl.cezarysanecki.parkingdomain.parking.model.parking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotBase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenParkingSpotFactory {

    public static OpenParkingSpot create(ParkingSpotBase parkingSpot) {
        return new OpenParkingSpot(
                parkingSpot,
                OpenParkingSpotPolicy.allCurrentPolicies());
    }

}
