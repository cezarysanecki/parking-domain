package pl.cezarysanecki.parkingdomain.parkingspot.view.model;

import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotLeft;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupied;

import java.util.Set;

public interface ParkingSpotViews {

    Set<ParkingSpotView> queryForAvailableParkingSpots();

    void handle(ParkingSpotCreated event);

    void handle(ParkingSpotOccupied event);

    void handle(ParkingSpotLeft event);

}
