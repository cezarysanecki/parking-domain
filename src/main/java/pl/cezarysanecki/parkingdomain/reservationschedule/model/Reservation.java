package pl.cezarysanecki.parkingdomain.reservationschedule.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.client.model.ClientId;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Value
public class Reservation {

    ReservationId reservationId;
    ReservationSlot reservationSlot;
    ClientId clientId;

    public boolean intersects(ReservationSlot slot) {
        return reservationSlot.intersects(slot);
    }

    long minutesTo(LocalDateTime now) {
        long minutes = ChronoUnit.MINUTES.between(now, reservationSlot.getSince());
        return minutes > 0 ? minutes : 0;
    }

}
