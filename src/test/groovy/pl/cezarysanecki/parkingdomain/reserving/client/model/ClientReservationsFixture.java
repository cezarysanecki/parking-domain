package pl.cezarysanecki.parkingdomain.reserving.client.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientReservations noClientReservations() {
        return new ClientReservations(ClientId.newOne(), Set.of());
    }

    public static ClientReservations clientReservationsWithReservation(ReservationId reservationId) {
        return new ClientReservations(ClientId.newOne(), Set.of(reservationId));
    }

}
