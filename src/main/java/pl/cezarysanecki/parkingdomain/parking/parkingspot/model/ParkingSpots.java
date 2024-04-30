package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;

public interface ParkingSpots {

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    Option<ParkingSpot> findBy(VehicleId vehicleId);

    void publish(ParkingSpotEvent parkingSpotEvent);

}
