package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.Instant;

class ProductionDateProvider implements DateProvider {

  @Override
  public Instant now() {
    return Instant.now();
  }

}
