package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;

public interface ReservationRequestsRepository {

  void publish(ReservationRequestsEvent event);

  Option<ReservationRequests> findBy(ReservationRequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId);

  Option<ReservationRequests> findBy(ReservationRequestId reservationRequestId);

  default ReservationRequests getBy(ReservationRequestId reservationRequestId) {
    return findBy(reservationRequestId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requests by request with id: " + reservationRequestId));
  }
}
