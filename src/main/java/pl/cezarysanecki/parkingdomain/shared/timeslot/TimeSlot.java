package pl.cezarysanecki.parkingdomain.shared.timeslot;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public record TimeSlot(Instant from, Instant to) {

  public TimeSlot {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("from cannot be after to");
    }
  }

  public static TimeSlot createTimeSlot(Instant date, int fromHour, int toHour) {
    ZonedDateTime zonedDateTime = date.atZone(ZoneOffset.systemDefault());

    Instant from = zonedDateTime.withHour(fromHour).withMinute(0).withSecond(0).withNano(0).toInstant();
    Instant to = zonedDateTime.withHour(toHour).withMinute(0).withSecond(0).withNano(0).toInstant();

    return new TimeSlot(from, to);
  }

  public boolean within(TimeSlot other) {
    return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
  }

}
