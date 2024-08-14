package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public interface DateProvider {

  ZoneId ZONE_OFFSET = ZoneOffset.systemDefault();

  Instant now();

  default Instant fromNow(Duration duration) {
    return now().plus(duration);
  }

}
