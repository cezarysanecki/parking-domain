package pl.cezarysanecki.parkingdomain.requesting;

import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.requesting.api.ReservationRequestId;

public interface ReservationRequesterRepository {

  void saveNew(RequesterId requesterId, int limit);

  ReservationRequester findBy(RequesterId requesterId);

  void saveCheckingVersion(ReservationRequester requester);

  ReservationRequester findBy(ReservationRequestId reservationRequestId);

}
