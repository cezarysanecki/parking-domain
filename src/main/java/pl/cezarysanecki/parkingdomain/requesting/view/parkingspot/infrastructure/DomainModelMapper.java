package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotReservationRequestsView;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotReservationRequestsView map(ParkingSpotReservationRequestsViewEntity entity) {
        return new ParkingSpotReservationRequestsView(
                entity.parkingSpotId,
                entity.capacity - entity.currentOccupation(),
                entity.currentReservations.stream()
                        .map(vehicleReservationEntity -> vehicleReservationEntity.reservationId)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
