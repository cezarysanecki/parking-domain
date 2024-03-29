package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;

public interface ParkingSpots {

    Option<OpenParkingSpot> findOpenBy(ParkingSpotId parkingSpotId);

    Option<OccupiedParkingSpot> findOccupiedBy(VehicleId vehicleId);

    void publish(ParkingSpotEvent parkingSpotEvent);

}
