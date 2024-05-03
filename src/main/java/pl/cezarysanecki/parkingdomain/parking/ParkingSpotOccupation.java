package pl.cezarysanecki.parkingdomain.parking;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;

@Value
public class ParkingSpotOccupation {

    int currentOccupationLevel;
    int capacity;

    public ParkingSpotOccupation(int currentOccupationLevel, int capacity) {
        if (capacity < currentOccupationLevel) {
            throw new IllegalArgumentException("capacity must be greater than current occupation level");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (currentOccupationLevel < 0) {
            throw new IllegalArgumentException("current occupation level cannot be negative");
        }

        this.currentOccupationLevel = currentOccupationLevel;
        this.capacity = capacity;
    }

    public boolean cannotHandle(SpotUnits spotUnits) {
        return currentOccupationLevel + spotUnits.getValue() > capacity;
    }

    public ParkingSpotOccupation handle(SpotUnits spotUnits) {
        return new ParkingSpotOccupation(currentOccupationLevel + spotUnits.getValue(), capacity);
    }

    public boolean isFull() {
        return currentOccupationLevel == 0;
    }

}
