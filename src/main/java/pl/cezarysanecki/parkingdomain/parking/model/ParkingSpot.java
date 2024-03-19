package pl.cezarysanecki.parkingdomain.parking.model;

public interface ParkingSpot {

    ParkingSpotBase getBase();

    default boolean isEmpty() {
        return getBase().isEmpty();
    }

    default boolean isFull() {
        return getBase().isFull();
    }

}
