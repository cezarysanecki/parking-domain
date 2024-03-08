package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;

public interface ParkingSpots {

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    ParkingSpot publish(ParkingSpotEvent event);

}
