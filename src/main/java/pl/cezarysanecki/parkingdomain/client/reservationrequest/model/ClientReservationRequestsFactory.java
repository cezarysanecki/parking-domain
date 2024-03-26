package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
public class ClientReservationRequestsFactory {

    private final DateProvider dateProvider;

    public ClientReservationRequests createEmpty(ClientId clientId) {
        LocalDateTime now = dateProvider.now();
        return ClientReservationRequests.empty(clientId, now);
    }

    public ClientReservationRequests create(ClientId clientId, Set<ReservationId> reservations) {
        LocalDateTime now = dateProvider.now();
        return new ClientReservationRequests(clientId, reservations, now);
    }

}
