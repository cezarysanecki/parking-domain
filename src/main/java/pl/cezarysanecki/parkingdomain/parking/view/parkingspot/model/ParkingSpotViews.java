package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Released;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.Occupied;

import java.util.Set;

public interface ParkingSpotViews {

    Set<ParkingSpotView> queryForAvailableParkingSpots();

    void handle(ParkingSpotAdded event);

    void handle(Occupied event);

    void handle(Released event);

}
