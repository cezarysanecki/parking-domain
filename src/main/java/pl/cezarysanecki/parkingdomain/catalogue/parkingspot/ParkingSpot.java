package pl.cezarysanecki.parkingdomain.catalogue.parkingspot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class ParkingSpot {

    @NonNull
    ParkingSpotId parkingSpotId;
    @NonNull
    ParkingSpotCapacity capacity;
    @NonNull
    ParkingSpotCategory category;

    ParkingSpot(UUID parkingSpotId, int capacity, ParkingSpotCategory category) {
        this(ParkingSpotId.of(parkingSpotId), ParkingSpotCapacity.of(capacity), category);
    }

}
