package pl.cezarysanecki.parkingdomain.poc;

import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value
class FullyOccupiedParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    int capacity;
    @NonNull
    List<VehicleId> parkedVehicles;

    FullyOccupiedParkingSpot(
            ParkingSpotId parkingSpotId,
            int capacity,
            List<VehicleId> parkedVehicles) {
        if (capacity <= 0) {
            throw new IllegalStateException("capacity must be positive");
        }
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new ArrayList<>(parkedVehicles);
    }

    ParkingSpot revokeAccessFrom(VehicleId vehicleId) {
        parkedVehicles.remove(vehicleId);

        if (parkedVehicles.isEmpty()) {
            return new FreeParkingSpot(parkingSpotId, capacity);
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
    }

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

    List<VehicleId> getParkedVehicles() {
        return Collections.unmodifiableList(parkedVehicles);
    }

}
