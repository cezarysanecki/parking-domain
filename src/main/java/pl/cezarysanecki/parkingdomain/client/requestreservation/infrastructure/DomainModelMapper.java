package pl.cezarysanecki.parkingdomain.client.requestreservation.infrastructure;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestId;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsFactory;

import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DomainModelMapper {

    private final ClientReservationRequestsFactory clientReservationRequestsFactory;

    ClientReservationRequests map(ClientReservationsEntity entity) {
        return clientReservationRequestsFactory.create(
                ClientId.of(entity.clientId),
                entity.clientReservationRequests.stream()
                        .map(ClientReservationRequestId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
