package pl.cezarysanecki.parkingdomain.requesting.model.requester;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requesting.Requester;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRequesterFixture {

  public static Requester requesterWithNoReservationRequests(int limit) {
    return new Requester(RequesterId.newOne(), 0, limit, Version.zero());
  }

  public static Requester requesterWith(ReservationRequestId reservationRequestId) {
    return new Requester(RequesterId.newOne(), 1, 1, Version.zero());
  }

}
