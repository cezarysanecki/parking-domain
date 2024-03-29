package pl.cezarysanecki.parkingdomain.parkingspot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.vehicles.VehicleSize;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotOccupation {

    int currentOccupation;
    int capacity;

    public static ParkingSpotOccupation of(int currentOccupation, int capacity) {
        if (currentOccupation < 0) {
            throw new IllegalArgumentException("current occupation cannot be negative");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("parking spot capacity must be positive");
        }
        if (currentOccupation > capacity) {
            throw new IllegalArgumentException("occupation cannot be grater than capacity");
        }
        return new ParkingSpotOccupation(currentOccupation, capacity);
    }

    public boolean canHandle(VehicleSize vehicleSize) {
        return currentOccupation + vehicleSize.getValue() <= capacity;
    }

}
