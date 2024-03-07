package pl.cezarysanecki.parkingdomain.poc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FullyOccupiedParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final List<VehicleId> parkedVehicles;

    FullyOccupiedParkingSpot(
            ParkingSpotId parkingSpotId,
            int capacity,
            List<VehicleId> parkedVehicles) {
        this.parkingSpotId = parkingSpotId;
        this.capacity = capacity;
        this.parkedVehicles = new ArrayList<>(parkedVehicles);
    }

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

    public ParkingSpot revokeAccessFrom(VehicleId vehicleId) {
        parkedVehicles.remove(vehicleId);

        if (parkedVehicles.isEmpty()) {
            return new FreeParkingSpot(parkingSpotId, capacity);
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, Collections.unmodifiableList(parkedVehicles));
    }

}
