package pl.cezarysanecki.parkingdomain.poc;

import java.util.Set;

class FreeParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final int capacity;

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
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

}
