package pl.cezarysanecki.parkingdomain.views.parking.model;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;

public interface ParkingViews {

    AvailableParkingSpotsView findAvailable();

    void addParkingSpot(ParkingSpotId parkingSpotId, ParkingSpotType parkingSpotType, int capacity);

    void increaseCapacity(ParkingSpotId parkingSpotId, int delta);

    void decreaseCapacity(ParkingSpotId parkingSpotId, int delta);

    void removeParkingSpot(ParkingSpotId parkingSpotId);

}
