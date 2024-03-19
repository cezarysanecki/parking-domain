package pl.cezarysanecki.parkingdomain.parkingview.model;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public interface ParkingViews {

    AvailableParkingSpotsView findAvailable();

    void addParkingSpot(ParkingSpotId parkingSpotId, int capacity);

    void increaseCapacity(ParkingSpotId parkingSpotId, int delta);

    void decreaseCapacity(ParkingSpotId parkingSpotId, int delta);

    void removeParkingSpot(ParkingSpotId parkingSpotId);

}
