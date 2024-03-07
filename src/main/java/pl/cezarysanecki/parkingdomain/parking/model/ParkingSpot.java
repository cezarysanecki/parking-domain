package pl.cezarysanecki.parkingdomain.parking.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

interface ParkingSpot {

    ParkingSpotId getParkingSpotId();

    int getCapacity();

    Set<VehicleId> getParkedVehicles();

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
