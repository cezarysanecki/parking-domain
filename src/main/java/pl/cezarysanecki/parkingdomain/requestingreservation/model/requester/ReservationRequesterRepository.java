package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

public interface ReservationRequesterRepository {

  void saveNew(ReservationRequesterId requesterId, int limit);

  Option<ReservationRequester> findBy(ReservationRequesterId reservationRequesterId);

}
