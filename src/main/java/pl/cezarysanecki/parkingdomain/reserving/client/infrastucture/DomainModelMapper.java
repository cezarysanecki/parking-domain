package pl.cezarysanecki.parkingdomain.reserving.client.infrastucture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ClientReservations map(ClientReservationsEntity entity) {
        return new ClientReservations(
                ClientId.of(entity.clientId),
                entity.reservations.stream()
                        .map(ReservationId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
