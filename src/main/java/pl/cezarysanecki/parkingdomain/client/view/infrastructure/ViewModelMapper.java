package pl.cezarysanecki.parkingdomain.client.view.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.client.view.model.ClientReservationsView;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ViewModelMapper {
    static ClientReservationsView map(ClientReservationsViewEntity entity) {
        return new ClientReservationsView(entity.getClientId(), entity.getReservations()
                .stream()
                .map(reservationEntity -> new ClientReservationsView.Reservation(
                        reservationEntity.reservationId,
                        reservationEntity.parkingSpotId,
                        reservationEntity.status))
                .collect(Collectors.toUnmodifiableSet()));
    }
}
