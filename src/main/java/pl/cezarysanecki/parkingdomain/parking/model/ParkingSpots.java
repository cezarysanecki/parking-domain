package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;

public interface ParkingSpots {

    Option<ParkingSpot> tryFindBy(ParkingSpotId parkingSpotId);

    default ParkingSpot findBy(ParkingSpotId parkingSpotId) {
        return tryFindBy(parkingSpotId)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find parking spot with id: " + parkingSpotId));
    }

    ParkingSpot publish(ParkingSpotEvent event);

}
