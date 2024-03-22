package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ClientReservationRequestsFactory {

    private final DateProvider dateProvider;

    public ClientReservationRequests createEmpty(ClientId clientId) {
        LocalDateTime now = dateProvider.now();
        return ClientReservationRequests.empty(clientId, now);
    }

}
