package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotLeft;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupied;

import java.util.Set;

public interface ParkingSpotViews {

    Set<ParkingSpotView> queryForAvailableParkingSpots();

    void handle(ParkingSpotAdded event);

    void handle(ParkingSpotOccupied event);

    void handle(ParkingSpotLeft event);

}
