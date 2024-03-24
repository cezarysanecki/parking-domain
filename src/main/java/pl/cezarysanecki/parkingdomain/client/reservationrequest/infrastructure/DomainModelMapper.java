package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFactory;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DomainModelMapper {

    private final ClientReservationRequestsFactory clientReservationRequestsFactory;

    ClientReservationRequests map(ClientReservationsEntity entity) {
        return clientReservationRequestsFactory.create(
                ClientId.of(entity.clientId),
                entity.clientReservations.stream()
                        .map(ReservationId::of)
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
