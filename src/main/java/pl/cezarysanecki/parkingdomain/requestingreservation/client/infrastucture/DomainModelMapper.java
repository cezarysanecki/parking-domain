package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ClientReservationRequests map(ClientReservationRequestsEntity entity) {
        return new ClientReservationRequests(
                ClientId.of(entity.clientId),
                entity.reservations.stream()
                        .map(ReservationId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
