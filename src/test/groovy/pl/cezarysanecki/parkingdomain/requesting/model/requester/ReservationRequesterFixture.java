package pl.cezarysanecki.parkingdomain.requesting.model.requester;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requesting.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRequesterFixture {

  public static ReservationRequester requesterWithNoReservationRequests(int limit) {
    return new ReservationRequester(RequesterId.newOne(), 0, limit, Version.zero());
  }

  public static ReservationRequester requesterWith(ReservationRequestId reservationRequestId) {
    return new ReservationRequester(RequesterId.newOne(), 1, 1, Version.zero());
  }

}
