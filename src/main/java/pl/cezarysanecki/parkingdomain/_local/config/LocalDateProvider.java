package pl.cezarysanecki.parkingdomain._local.config;

import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.Duration;
import java.time.Instant;

public class LocalDateProvider implements DateProvider {

  private Instant currentDateTime = Instant.now();

  @Override
  public Instant now() {
    return currentDateTime;
  }

  public Instant setCurrentDate(Instant localDateTime) {
    currentDateTime = localDateTime;
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
