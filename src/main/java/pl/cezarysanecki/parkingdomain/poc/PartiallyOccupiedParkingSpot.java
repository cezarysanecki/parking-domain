package pl.cezarysanecki.parkingdomain.poc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class PartiallyOccupiedParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final Set<VehicleId> parkedVehicles;

    PartiallyOccupiedParkingSpot(
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

    ParkingSpot occupyBy(VehicleId vehicleId) {
        parkedVehicles.add(vehicleId);

        if (parkedVehicles.size() == capacity) {
            return new FullyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
    }

    ParkingSpot leaveBy(VehicleId vehicleId) {
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

    Set<VehicleId> getParkedVehicles() {
        return Collections.unmodifiableSet(parkedVehicles);
    }

}
