package pl.cezarysanecki.parkingdomain.parking.model;

import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Value
class OverOccupiedParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    int capacity;
    @NonNull
    Set<VehicleId> parkedVehicles;

    OverOccupiedParkingSpot(
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

        if (capacity < parkedVehicles.size()) {
            return new OverOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
        }
        return new FullyOccupiedParkingSpot(parkingSpotId, capacity, getParkedVehicles());
    }

    @Override
    public Set<VehicleId> getParkedVehicles() {
        return Collections.unmodifiableSet(parkedVehicles);
    }

}
