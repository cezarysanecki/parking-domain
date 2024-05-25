package pl.cezarysanecki.parkingdomain.commons.date;

import java.time.LocalDateTime;

class ProductionDateProvider implements DateProvider {

  @Override
  public LocalDateTime now() {
    return LocalDateTime.now();
  }

}
