package pl.cezarysanecki.parkingdomain.reservation.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Instant;
import java.util.List;

public record ReservationsActivated(
    List<Entry> reservations
) implements DomainEvent {

  public record Entry(
      ReservationId reservationId,
      ReservationOwnerId reservationOwnerId,
      ParkingSpotId parkingSpotId,
      Instant startDate,
      SpotUnits spotUnits
  ) {
  }

}
