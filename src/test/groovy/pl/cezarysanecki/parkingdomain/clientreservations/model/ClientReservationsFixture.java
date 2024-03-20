package pl.cezarysanecki.parkingdomain.clientreservations.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientReservations noReservations(ClientId clientId, LocalDateTime now) {
        return new ClientReservations(clientId, Set.of(), now);
    }

    public static ClientReservations reservationsWith(ClientId clientId, ReservationId reservationId, LocalDateTime now) {
        return new ClientReservations(clientId, Set.of(reservationId), now);
    }

    public static ClientReservations reservationsWith(ClientId clientId, LocalDateTime now) {
        return new ClientReservations(clientId, Set.of(), now);
    }

    public static ClientId anyClientId() {
        return ClientId.of(UUID.randomUUID());
    }

}
