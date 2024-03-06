package pl.cezarysanecki.parkingdomain.poc;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FreeParkingSpot implements ParkingSpot {

    private final ParkingSpotId parkingSpotId;

    @Override
    public ParkingSpotId parkingSpotId() {
        return parkingSpotId;
    }

}
