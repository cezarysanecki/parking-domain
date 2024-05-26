package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRequesterFixture {

  public static ReservationRequester requesterWithNoReservationRequests(int limit) {
    return new ReservationRequester(ReservationRequesterId.newOne(), HashSet.empty(), limit);
  }

  public static ReservationRequester requesterWith(ReservationRequestId reservationRequestId) {
    return new ReservationRequester(ReservationRequesterId.newOne(), HashSet.of(reservationRequestId), 1);
  }

}
