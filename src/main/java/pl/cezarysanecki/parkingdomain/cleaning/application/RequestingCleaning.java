package pl.cezarysanecki.parkingdomain.cleaning.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.application.ProvidingUsageOfParkingSpot;

@Slf4j
@RequiredArgsConstructor
public class RequestingCleaning {

    private final ProvidingUsageOfParkingSpot providingUsageOfParkingSpot;

    public void requestCleaning(ParkingSpotId parkingSpotId) {
        log.debug("cleaning make parking spot with id {} of use", parkingSpotId);
        providingUsageOfParkingSpot.makeOutOfUse(parkingSpotId);
    }

}
