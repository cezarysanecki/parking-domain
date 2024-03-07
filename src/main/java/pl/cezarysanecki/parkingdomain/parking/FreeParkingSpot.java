package pl.cezarysanecki.parkingdomain.parking;

import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Value
class FreeParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    int capacity;

    FreeParkingSpot(
            ParkingSpotId parkingSpotId,
            int capacity
    ) {
        if (capacity <= 0) {
            throw new IllegalStateException("capacity must be positive");
        }
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
    }

    ParkingSpot occupyBy(VehicleId vehicleId) {
        if (capacity == 1) {
            return new FullyOccupiedParkingSpot(parkingSpotId, capacity, Set.of(vehicleId));
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, Set.of(vehicleId));
    }

    @Override
    public Set<VehicleId> getParkedVehicles() {
        return Set.of();
    }

}
