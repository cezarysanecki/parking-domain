package pl.cezarysanecki.parkingdomain.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.Duration;
import java.time.Instant;

public interface BusinessDateProvider {

  Instant provideDateForActivatingReservations();

  Instant provideDateForMarkingReservationsAsNotUsed();

  @Component
  class PropertiesBusinessDateProvider implements BusinessDateProvider {

    private final int minutesToConsiderReservationActive;
    private final int minutesToConsiderReservationNotUsed;

    private final DateProvider dateProvider;

    PropertiesBusinessDateProvider(
        @Value("${business.parking.minutes-to-consider-reservation-active}") int minutesToConsiderReservationActive,
        @Value("${business.parking.minutes-to-consider-reservation-not-used}") int minutesToConsiderReservationNotUsed,
        DateProvider dateProvider) {
      this.minutesToConsiderReservationActive = minutesToConsiderReservationActive;
      this.minutesToConsiderReservationNotUsed = minutesToConsiderReservationNotUsed;
      this.dateProvider = dateProvider;
    }

    @Override
    public Instant provideDateForActivatingReservations() {
      return dateProvider.now().plus(Duration.ofMinutes(minutesToConsiderReservationActive));
    }

    @Override
    public Instant provideDateForMarkingReservationsAsNotUsed() {
      return dateProvider.now().minus(Duration.ofMinutes(minutesToConsiderReservationNotUsed));
    }
  }

}
