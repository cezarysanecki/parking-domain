package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ClientReservationsFactory {

    private final DateProvider dateProvider;

    public ClientReservations createEmpty(ClientId clientId) {
        LocalDateTime now = dateProvider.now();
        return ClientReservations.empty(clientId, now);
    }

}
