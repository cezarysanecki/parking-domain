package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ClientReservationRequests map(ClientReservationsEntity entity) {
        return new ClientReservationRequests(
                ClientId.of(entity.clientId),
                entity.clientReservations.stream()
                        .map(ReservationId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
