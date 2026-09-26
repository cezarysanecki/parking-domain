package pl.cezarysanecki.parkingdomain.shared;

import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.Instant;
import java.time.LocalTime;

/**
 * Daily schedule of the parking (local time, {@link DateProvider#ZONE_OFFSET}):
 * <ul>
 *   <li>05:00-24:00 - parking spots can be occupied,</li>
 *   <li>00:00-01:00 - no new occupations, occupants are reminded to release parking spots,</li>
 *   <li>01:00-05:00 - technical break, remaining occupations are released by force (towing).</li>
 * </ul>
 */
public final class ParkingOpeningHours {

  public static final LocalTime OPENING = LocalTime.of(5, 0);
  public static final LocalTime OCCUPYING_END = LocalTime.MIDNIGHT;
  public static final LocalTime CLOSING = LocalTime.of(1, 0);

  private ParkingOpeningHours() {
  }

  public static boolean canOccupyAt(Instant instant) {
    return isWithin(instant, OPENING, OCCUPYING_END);
  }

  public static boolean isTimeToRemindAboutReleasing(Instant instant) {
    return isWithin(instant, OCCUPYING_END, CLOSING);
  }

  public static boolean isTechnicalBreak(Instant instant) {
    return isWithin(instant, CLOSING, OPENING);
  }

  private static boolean isWithin(Instant instant, LocalTime from, LocalTime to) {
    LocalTime time = instant.atZone(DateProvider.ZONE_OFFSET).toLocalTime();
    if (from.isBefore(to)) {
      return !time.isBefore(from) && time.isBefore(to);
    }
    return !time.isBefore(from) || time.isBefore(to);
  }

}
