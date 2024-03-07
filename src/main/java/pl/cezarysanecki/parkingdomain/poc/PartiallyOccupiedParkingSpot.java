package pl.cezarysanecki.parkingdomain.poc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PartiallyOccupiedParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final List<VehicleId> parkedVehicles;

    PartiallyOccupiedParkingSpot(
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

    ParkingSpot grantAccessFor(VehicleId vehicleId) {
        parkedVehicles.add(vehicleId);

        if (parkedVehicles.size() == capacity) {
            return new FullyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
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
