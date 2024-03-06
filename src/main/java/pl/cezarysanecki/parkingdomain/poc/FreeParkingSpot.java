package pl.cezarysanecki.parkingdomain.poc;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class FreeParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final int capacity;

    ParkingSpot grantAccessFor(VehicleId vehicleId) {
        if (capacity == 1) {
            return new FullyOccupiedParkingSpot(parkingSpotId, capacity, List.of(vehicleId));
        }
        return new PartiallyOccupiedParkingSpot(parkingSpotId, capacity, List.of(vehicleId));
    }

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

}
