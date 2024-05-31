package pl.cezarysanecki.parkingdomain.parking.model.reservation;

import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public interface ReservationEvent extends DomainEvent {

  ReservationId reservationId();

  record ReservationFulfilled(
      @NonNull ReservationId reservationId
  ) implements ReservationEvent {
  }

}
