package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;

public interface ReservationRequesterEvent {

    ReservationRequesterId requesterId();

    record ReservationRequestCreated(
            ReservationRequesterId requesterId,
            int limit
    ) implements ReservationRequesterEvent {
    }

    record ReservationRequestAppended(
            ReservationRequesterId requesterId,
            ReservationRequestId reservationRequestId
    ) implements ReservationRequesterEvent {
    }

    record ReservationRequestRemoved(
            ReservationRequesterId requesterId,
            ReservationRequestId reservationRequestId
    ) implements ReservationRequesterEvent {
    }

}
