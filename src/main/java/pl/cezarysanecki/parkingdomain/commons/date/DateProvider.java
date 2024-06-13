package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public interface DateProvider {

  ZoneId ZONE_OFFSET = ZoneOffset.systemDefault();

  Instant now();

  default Instant tomorrowMidnight() {
    return ZonedDateTime.ofInstant(now(), ZONE_OFFSET)
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .plusDays(1)
        .toInstant();
  }

  default Instant nearestFutureDateAt(int hour) {
    ZonedDateTime zonedNow = ZonedDateTime.ofInstant(now(), ZoneId.systemDefault());
    if (zonedNow.toLocalTime().getHour() < hour) {
      return zonedNow
          .withHour(hour)
          .withMinute(0)
          .withSecond(0)
          .withNano(0)
          .toInstant();
    }
    return zonedNow
        .withHour(hour)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .plusDays(1)
        .toInstant();
  }

}
