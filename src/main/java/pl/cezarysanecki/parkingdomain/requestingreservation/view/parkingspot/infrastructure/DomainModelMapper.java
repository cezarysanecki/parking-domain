package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationsView;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotReservationsView map(ParkingSpotReservationsViewEntity entity) {
        return new ParkingSpotReservationsView(
                entity.parkingSpotId,
                entity.capacity - entity.currentOccupation(),
                entity.currentReservations.stream()
                        .map(vehicleReservationEntity -> vehicleReservationEntity.reservationId)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
