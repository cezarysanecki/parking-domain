package pl.cezarysanecki.parkingdomain.cleaning.model;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

public interface CleaningRepository {

    CurrentCounterValue increaseCounterFor(ParkingSpotId parkingSpotId);

    void resetCounter(ParkingSpotId parkingSpotId);

}
