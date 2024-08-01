package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;

public interface ReservationRequestsEvent {

  record ReservationRequestMade(
      ReservationRequest reservationRequest,
      Version requesterVersion,
      Version timeSlotVersion
  ) implements ReservationRequestsEvent {
  }

}
