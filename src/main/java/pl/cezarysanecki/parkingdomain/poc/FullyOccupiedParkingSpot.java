package pl.cezarysanecki.parkingdomain.poc;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class FullyOccupiedParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;
    private final int capacity;
    private final List<VehicleId> parkedVehicles;

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

}
