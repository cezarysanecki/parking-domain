package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Value
public class Reservation {

    ReservationId reservationId;
    ReservationSlot reservationSlot;
    Set<Vehicle> vehicles;

    public boolean intersects(ReservationSlot slot) {
        return reservationSlot.intersects(slot);
    }

    long minutesTo(LocalDateTime now) {
        long minutes = ChronoUnit.MINUTES.between(now, reservationSlot.getSince());
        return minutes > 0 ? minutes : 0;
    }

}
