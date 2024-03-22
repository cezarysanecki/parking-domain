package pl.cezarysanecki.parkingdomain.client.requestreservation.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientReservationRequests noReservations(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public static ClientReservationRequests reservationsWith(ClientId clientId, ReservationId reservationId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(reservationId), now);
    }

    public static ClientReservationRequests reservationsWith(ClientId clientId, LocalDateTime now) {
        return new ClientReservationRequests(clientId, Set.of(), now);
    }

    public static ClientId anyClientId() {
        return ClientId.of(UUID.randomUUID());
    }

}
