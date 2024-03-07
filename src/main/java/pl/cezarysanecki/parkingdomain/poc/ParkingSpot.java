package pl.cezarysanecki.parkingdomain.poc;

import java.util.Collection;
import java.util.HashSet;

interface ParkingSpot {

    ParkingSpotId parkingSpotId();

    static ParkingSpot resolve(
            ParkingSpotId parkingSpotId,
            int capacity,
            Collection<VehicleId> parkedVehicles) {
        if (parkedVehicles.isEmpty()) {
            return new FreeParkingSpot(parkingSpotId, capacity);
        }
        if (capacity > parkedVehicles.size()) {
            return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, new HashSet<>(parkedVehicles));
        }
        if (capacity == parkedVehicles.size()) {
            return new FullyOccupiedParkingSpot(parkingSpotId, capacity, new HashSet<>(parkedVehicles));
        }
        return new OverOccupiedParkingSpot(parkingSpotId, capacity, new HashSet<>(parkedVehicles));
    }

}
