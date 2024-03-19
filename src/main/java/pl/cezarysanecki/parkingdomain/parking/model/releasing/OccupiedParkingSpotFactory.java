package pl.cezarysanecki.parkingdomain.parking.model.releasing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotBase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OccupiedParkingSpotFactory {

    public static OccupiedParkingSpot create(ParkingSpotBase parkingSpot) {
        return new OccupiedParkingSpot(parkingSpot);
    }

}
