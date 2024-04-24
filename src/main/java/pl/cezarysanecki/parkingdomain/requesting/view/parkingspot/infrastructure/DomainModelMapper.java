package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsView;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotRequestsView map(ParkingSpotRequestsViewEntity entity) {
        return new ParkingSpotRequestsView(
                entity.parkingSpotId,
                entity.capacity - entity.currentOccupation(),
                entity.currentRequests.stream()
                        .map(vehicleRequestEntity -> vehicleRequestEntity.requestId)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
