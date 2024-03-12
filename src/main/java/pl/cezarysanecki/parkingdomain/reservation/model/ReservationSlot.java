package pl.cezarysanecki.parkingdomain.reservation.model;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ReservationSlot {

    LocalDateTime since;
    int hours;

    public ReservationSlot(LocalDateTime since, int hours) {
        if (hours > 12) {
            throw new IllegalArgumentException("reservation cannot be longer then 12 hours");
        }

        this.since = since;
        this.hours = hours;
    }

    public LocalDateTime until() {
        return since.plusHours(hours);
    }

    public boolean intersects(ReservationSlot reservationSlot) {
        return until().isAfter(reservationSlot.since) && since.isBefore(reservationSlot.until());
    }

}
