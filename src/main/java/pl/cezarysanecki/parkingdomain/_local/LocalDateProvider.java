package pl.cezarysanecki.parkingdomain._local;

import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateProvider implements DateProvider {

  private Instant currentDateTime = Instant.now();

  @Override
  public Instant now() {
    return currentDateTime;
  }

  public Instant setCurrentDate(LocalDate localDate) {
    currentDateTime = ZonedDateTime.of(localDate, LocalTime.of(0, 0), ZoneId.systemDefault()).toInstant();
    return currentDateTime;
  }

  public Instant passHours(int hours) {
    currentDateTime = currentDateTime.plus(Duration.ofHours(hours));
    return currentDateTime;
  }

  public Instant passMinutes(int minutes) {
    currentDateTime = currentDateTime.plus(Duration.ofMinutes(minutes));
    return currentDateTime;
  }

}
