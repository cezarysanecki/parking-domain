package pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest;

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;

public interface ReservationRequestsEvent {

  record ReservationRequestMade(
      ReservationRequest reservationRequest,
      Version requesterVersion,
      Version timeSlotVersion
  ) implements ReservationRequestsEvent {
  }

}
