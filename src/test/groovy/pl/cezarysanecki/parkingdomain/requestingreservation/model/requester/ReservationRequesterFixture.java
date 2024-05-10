package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRequesterFixture {

    public static ReservationRequester requesterWithNoReservationRequests() {
        return new ReservationRequester(ReservationRequesterId.newOne(), HashSet.empty());
    }

    public static ReservationRequester requesterWith(ReservationRequestId reservationRequestId) {
        return new ReservationRequester(ReservationRequesterId.newOne(), HashSet.of(reservationRequestId));
    }

}
