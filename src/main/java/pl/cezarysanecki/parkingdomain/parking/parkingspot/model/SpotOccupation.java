package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

public class SpotOccupation {

    int currentOccupation;
    int capacity;

    private SpotOccupation(int currentOccupation, int capacity) {
        if (currentOccupation < 0) {
            throw new IllegalArgumentException("current occupation cannot be negative");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("parking spot capacity must be positive");
        }
        if (currentOccupation > capacity) {
            throw new IllegalArgumentException("occupation cannot be grater than capacity");
        }
        this.currentOccupation = currentOccupation;
        this.capacity = capacity;
    }

    public static SpotOccupation of(int currentOccupation, int capacity) {
        return new SpotOccupation(currentOccupation, capacity);
    }

    public boolean cannotHandle(SpotUnits spotUnits) {
        return currentOccupation + spotUnits.getValue() > capacity;
    }

    public SpotOccupation occupyWith(SpotUnits spotUnits) {
        return new SpotOccupation(currentOccupation + spotUnits.getValue(), capacity);
    }

    public boolean isEmpty() {
        return currentOccupation == 0;
    }

    public boolean isFull() {
        return currentOccupation == capacity;
    }

}
