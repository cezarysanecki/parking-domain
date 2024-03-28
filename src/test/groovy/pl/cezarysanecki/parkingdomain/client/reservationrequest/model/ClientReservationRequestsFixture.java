package pl.cezarysanecki.parkingdomain.client.reservationrequest.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationRequestsFixture {

    public static ClientReservationRequests noReservationRequests(ClientId clientId) {
        return new ClientReservationRequests(clientId, Set.of());
    }

    public static ClientReservationRequests reservationRequestsWith(ClientId clientId, ReservationId reservationId) {
        return new ClientReservationRequests(clientId, Set.of(reservationId));
    }

    public static ReservationId anyReservationId() {
        return ReservationId.of(UUID.randomUUID());
    }

    public static ClientId anyClientId() {
        return ClientId.of(UUID.randomUUID());
    }

}
