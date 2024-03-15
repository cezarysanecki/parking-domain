package pl.cezarysanecki.parkingdomain.clientreservationsview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientReservationsView {

    UUID clientId;
    Set<Reservation> reservations;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reservation {

        UUID reservationId;
        UUID parkingSpotId;
        LocalDateTime since;
        LocalDateTime until;

    }

}
