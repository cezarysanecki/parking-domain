package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;

public interface ParkingSpots {

    Option<NormalParkingSpot> findBy(ParkingSpotId parkingSpotId);

    NormalParkingSpot publish(ParkingSpotEvent event);

}
