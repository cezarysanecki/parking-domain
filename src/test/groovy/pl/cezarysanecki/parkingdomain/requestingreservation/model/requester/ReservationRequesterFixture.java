package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRequesterFixture {

  public static ReservationRequester requesterWithNoReservationRequests(int limit) {
    return new ReservationRequester(ReservationRequesterId.newOne(), 0, limit, Version.zero());
  }

  public static ReservationRequester requesterWith(ReservationRequestId reservationRequestId) {
    return new ReservationRequester(ReservationRequesterId.newOne(), 1, 1, Version.zero());
  }

}
