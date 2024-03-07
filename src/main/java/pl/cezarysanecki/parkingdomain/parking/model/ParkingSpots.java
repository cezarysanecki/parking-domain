package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.Optional;

public interface ParkingSpots {

    Optional<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    void save(ParkingSpot parkingSpot);

}
