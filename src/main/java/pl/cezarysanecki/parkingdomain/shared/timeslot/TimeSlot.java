package pl.cezarysanecki.parkingdomain.shared.timeslot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public record TimeSlot(Instant from, Instant to) {

    public TimeSlot {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from cannot be after to");
        }
    }

    public static TimeSlot createTimeSlotAtUTC(LocalDate thisDay, int fromHour, int toHour) {
        Instant from = thisDay.atTime(fromHour, 0).toInstant(ZoneOffset.UTC);
        Instant to = thisDay.atTime(toHour, 0).toInstant(ZoneOffset.UTC);
        return new TimeSlot(from, to);
    }

}
