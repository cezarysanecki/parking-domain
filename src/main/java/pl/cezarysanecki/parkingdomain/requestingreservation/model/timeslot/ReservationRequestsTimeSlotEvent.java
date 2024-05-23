package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;

public interface ReservationRequestsTimeSlotEvent {

    ReservationRequestsTimeSlotId reservationRequestsTimeSlotId();

    record ReservationRequestAppended(
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            ReservationRequest reservationRequest
    ) implements ReservationRequestsTimeSlotEvent {
    }

    record ReservationRequestRemoved(
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            ReservationRequest reservationRequest
    ) implements ReservationRequestsTimeSlotEvent {
    }

}
