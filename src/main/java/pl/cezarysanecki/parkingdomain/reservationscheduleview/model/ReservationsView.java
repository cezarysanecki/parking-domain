package pl.cezarysanecki.parkingdomain.reservationscheduleview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsView {

    UUID clientId;
    Set<Reservation> reservations;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reservation {

        UUID reservationId;
        LocalDateTime since;
        LocalDateTime until;

    }

}
