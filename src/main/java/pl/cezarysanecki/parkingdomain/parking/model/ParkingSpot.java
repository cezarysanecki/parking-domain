package pl.cezarysanecki.parkingdomain.parking.model;

public interface ParkingSpot {

    ParkingSpotBase getBase();

    default ParkingSpotId getParkingSpotId() {
        return getBase().getParkingSpotId();
    }

    default boolean isEmpty() {
        return getBase().isEmpty();
    }

    default boolean isFull() {
        return getBase().isFull();
    }

}
