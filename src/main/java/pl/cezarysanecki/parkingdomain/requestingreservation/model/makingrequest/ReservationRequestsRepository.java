package pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;

public interface ReservationRequestsRepository {

  void publish(ReservationRequestsEvent event);

  Option<ReservationRequests> findBy(ReservationRequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId);

  default ReservationRequests getBy(ReservationRequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId) {
    return findBy(requesterId, timeSlotId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find reservation requests by requester with id: " + requesterId + " and time slot with id: " + timeSlotId));
  }

}
