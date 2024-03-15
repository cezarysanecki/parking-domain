package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.control.Option;

import java.time.Instant;

public interface ParkingSpots {

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId, Instant when);

    Option<ParkingSpot> findBy(VehicleId vehicleId);

    ParkingSpot publish(ParkingSpotEvent event);

}
