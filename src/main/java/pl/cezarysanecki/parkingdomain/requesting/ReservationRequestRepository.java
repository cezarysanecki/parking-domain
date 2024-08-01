package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.collection.List;
import io.vavr.control.Option;

import java.time.Instant;

public interface ReservationRequestRepository {

  void publish(ReservationRequestEvent event);

  Option<ReservationRequest> findBy(ReservationRequestId reservationRequestId);

  default ReservationRequest getBy(ReservationRequestId reservationRequestId) {
    return findBy(reservationRequestId)
        .getOrElseThrow(() -> new IllegalStateException("cannot find reservation request with id: " + reservationRequestId));
  }

  List<ReservationRequest> findAllValidSince(Instant date);

}
