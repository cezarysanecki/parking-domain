package pl.cezarysanecki.parkingdomain.reservation;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;

import java.util.List;

public record ReservationsRemoved(
    List<Entry> reservations
) implements DomainEvent {

  public List<ReservationId> reservationIds() {
    return reservations.stream()
        .map(Entry::reservationId)
        .toList();
  }

  public record Entry(
      ReservationId reservationId,
      ReservationOwnerId ownerId
  ) {
  }

}
