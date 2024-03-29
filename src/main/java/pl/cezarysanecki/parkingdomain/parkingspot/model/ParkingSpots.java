package pl.cezarysanecki.parkingdomain.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.vehicle.model.VehicleId;

public interface ParkingSpots {

    Option<OpenParkingSpot> findOpenBy(ParkingSpotId parkingSpotId);

    Option<OccupiedParkingSpot> findOccupiedBy(VehicleId vehicleId);

    ParkingSpot publish(ParkingSpotEvent parkingSpotEvent);

}
