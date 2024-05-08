package pl.cezarysanecki.parkingdomain.requestingreservation.web;

import java.util.List;
import java.util.UUID;

public interface ReservationRequesterViewRepository {

    List<ReservationRequesterView> queryForAllReservationRequesters();

    record ReservationRequesterView(
            UUID reservationRequesterId,
            List<UUID> reservationRequestId
    ) {
    }

}
