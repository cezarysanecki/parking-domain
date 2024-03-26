package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationRequestsFixture {

    public static ClientReservationRequests noReservationRequests(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public static ClientReservationRequests reservationRequestsWith(ClientId clientId, ReservationId reservationId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(reservationId), now);
    }

    public static ReservationId anyReservationId() {
        return ReservationId.of(UUID.randomUUID());
    }

    public static ClientId anyClientId() {
        return ClientId.of(UUID.randomUUID());
    }

}
