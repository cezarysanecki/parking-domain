package pl.cezarysanecki.parkingdomain.requestingreservation.client.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientReservationsFixture {

    public static ClientReservationRequests clientWithNoReservationRequests() {
        return new ClientReservationRequests(ClientId.newOne(), Set.of());
    }

    public static ClientReservationRequests clientWithReservationRequest(ReservationId reservationId) {
        return new ClientReservationRequests(ClientId.newOne(), Set.of(reservationId));
    }

}
