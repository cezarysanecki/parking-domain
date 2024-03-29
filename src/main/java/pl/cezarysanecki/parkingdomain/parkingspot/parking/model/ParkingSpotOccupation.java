package pl.cezarysanecki.parkingdomain.parkingspot.parking.model;

import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleSize;

public class ParkingSpotOccupation {

    int currentOccupation;
    int capacity;

    private ParkingSpotOccupation(int currentOccupation, int capacity) {
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

    public static ParkingSpotOccupation of(int currentOccupation, int capacity) {
        return new ParkingSpotOccupation(currentOccupation, capacity);
    }

    public boolean canHandle(VehicleSize vehicleSize) {
        return currentOccupation + vehicleSize.getValue() <= capacity;
    }

    public ParkingSpotOccupation occupyWith(VehicleSize vehicleSize) {
        return new ParkingSpotOccupation(currentOccupation + vehicleSize.getValue(), capacity);
    }

    public boolean isFull() {
        return currentOccupation == capacity;
    }

}
