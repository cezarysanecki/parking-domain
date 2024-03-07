package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Value
class FullyOccupiedParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    int capacity;
    @NonNull
    Set<VehicleId> parkedVehicles;

    FullyOccupiedParkingSpot(
            ParkingSpotId parkingSpotId,
            int capacity,
            Set<VehicleId> parkedVehicles) {
        if (capacity <= 0) {
            throw new IllegalStateException("capacity must be positive");
        }
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new HashSet<>(parkedVehicles);
    }

    ParkingSpot leaveBy(VehicleId vehicleId) {
        parkedVehicles.remove(vehicleId);

        if (parkedVehicles.isEmpty()) {
            return new FreeParkingSpot(parkingSpotId, capacity);
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
    }

    @Override
    public Set<VehicleId> getParkedVehicles() {
        return Collections.unmodifiableSet(parkedVehicles);
    }

}
