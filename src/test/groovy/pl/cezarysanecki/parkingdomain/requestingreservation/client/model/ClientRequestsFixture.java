package pl.cezarysanecki.parkingdomain.requestingreservation.client.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.client.ClientId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientRequestsFixture {

    public static ReservationRequester clientWithNoRequests() {
        return new ReservationRequester(ClientId.newOne(), Set.of());
    }

    public static ReservationRequester clientWithRequest(ReservationRequestId reservationRequestId) {
        return new ReservationRequester(ClientId.newOne(), Set.of(reservationRequestId));
    }

}
