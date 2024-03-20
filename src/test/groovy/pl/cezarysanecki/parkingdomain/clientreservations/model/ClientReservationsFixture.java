package pl.cezarysanecki.parkingdomain.clientreservations.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientReservations noReservations(ClientId clientId) {
        return new ClientReservations(clientId, 0, LocalDateTime.now());
    }

    public static ClientReservations reservationsWith(ClientId clientId, int currentNumberOfReservations) {
        return new ClientReservations(clientId, currentNumberOfReservations, LocalDateTime.now());
    }

    public static ClientId anyClientId() {
        return ClientId.of(UUID.randomUUID());
    }

}
