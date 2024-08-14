package pl.cezarysanecki.parkingdomain.shared;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record TimeSlot(Instant from, Instant to) {

  public TimeSlot {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("from cannot be after to");
    }
  }

  public static TimeSlot create(LocalDate day, int fromHour, int toHour) {
    Instant from = ZonedDateTime.of(day, LocalTime.of(fromHour, 0), ZoneId.of("UTC")).toInstant();

    Instant to;
    if (toHour == 24) {
      to = ZonedDateTime.of(day.plusDays(1), LocalTime.of(0, 0), ZoneId.of("UTC")).toInstant();
    } else {
      to = ZonedDateTime.of(day, LocalTime.of(toHour, 0), ZoneId.of("UTC")).toInstant();
    }

    return new TimeSlot(from, to);
  }

  public boolean within(TimeSlot other) {
    return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
  }

  public boolean intersects(TimeSlot other) {
    return !(to.isBefore(other.from) || from.isAfter(other.to));
  }

}
