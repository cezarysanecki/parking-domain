package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import io.vavr.collection.List;
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

    record ReservationRequestMadeValid(
            ReservationRequesterEvent.ReservationRequestRemoved requesterEvent,
            ReservationRequestsTimeSlotEvent.ReservationRequestMadeValid timeSlotEvent
    ) implements ReservationRequestsEvent {

        public List<ReservationRequest> reservationRequests() {
            return timeSlotEvent.reservationRequests();
        }

    }

}
