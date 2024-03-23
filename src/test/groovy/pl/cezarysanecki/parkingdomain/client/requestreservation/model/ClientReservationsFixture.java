package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientReservationRequests noReservationRequests(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public static ClientReservationRequests reservationRequestsWith(ClientId clientId, ClientReservationRequestId clientReservationRequestId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(clientReservationRequestId), now);
    }

    public static ClientReservationRequests reservationRequestsWith(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public static ClientReservationRequestId anyClientReservationRequestId() {
        return ClientReservationRequestId.of(UUID.randomUUID());
    }

    public static ClientId anyClientId() {
        return ClientId.of(UUID.randomUUID());
    }

}
