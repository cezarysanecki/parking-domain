package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ParkingSpotRequestsViewEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleRequestEntity> currentRequests;

    int currentOccupation() {
        return currentRequests.stream()
                .map(vehicleRequestEntity -> vehicleRequestEntity.size)
                .reduce(0, Integer::sum);
    }

    @AllArgsConstructor
    static class VehicleRequestEntity {
        UUID requestId;
        int size;
    }

}
