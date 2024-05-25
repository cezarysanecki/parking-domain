package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

public interface ReservationRequesterEvent {

  ReservationRequesterId requesterId();

  record ReservationRequestAppended(
      ReservationRequesterId requesterId,
      ReservationRequestId reservationRequestId,
      Version requesterVersion
  ) implements ReservationRequesterEvent {
  }

}
