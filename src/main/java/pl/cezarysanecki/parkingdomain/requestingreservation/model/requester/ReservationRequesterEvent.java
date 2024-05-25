package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

public interface ReservationRequesterEvent {

  ReservationRequesterId requesterId();

  record ReservationRequestCreated(
      ReservationRequesterId requesterId,
      int limit
  ) implements ReservationRequesterEvent {
  }

  record ReservationRequestAppended(
      ReservationRequesterId requesterId,
      ReservationRequestId reservationRequestId,
      Version timeSlotVersion
  ) implements ReservationRequesterEvent {
  }

  record ReservationRequestRemoved(
      ReservationRequesterId requesterId,
      ReservationRequestId reservationRequestId,
      Version timeSlotVersion
  ) implements ReservationRequesterEvent {
  }

}
