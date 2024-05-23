package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent;

public interface ReservationRequestsEvent {

    record ReservationRequestMade(
            ReservationRequesterEvent.ReservationRequestAppended requesterEvent,
            ReservationRequestsTimeSlotEvent.ReservationRequestAppended timeSlotEvent
    ) implements ReservationRequestsEvent {

        public ReservationRequest reservationRequest() {
            return timeSlotEvent.reservationRequest();
        }

    }

    record ReservationRequestCancelled(
            ReservationRequesterEvent.ReservationRequestRemoved requesterEvent,
            ReservationRequestsTimeSlotEvent.ReservationRequestRemoved timeSlotEvent
    ) implements ReservationRequestsEvent {

        public ReservationRequest reservationRequest() {
            return timeSlotEvent.reservationRequest();
        }

    }

}
