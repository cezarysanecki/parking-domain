package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public interface DateProvider {

  ZoneId ZONE_OFFSET = ZoneOffset.systemDefault();

  Instant now();

  default LocalDate currentDay() {
    Instant now = now();
    ZonedDateTime zonedCurrentDateTime = now.atZone(ZONE_OFFSET);
    return zonedCurrentDateTime.toLocalDate();
  }

  default LocalDate nextDay() {
    return currentDay().plusDays(1);
  }

}
