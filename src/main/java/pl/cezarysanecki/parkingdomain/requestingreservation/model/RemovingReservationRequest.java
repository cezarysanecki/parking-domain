package pl.cezarysanecki.parkingdomain.requestingreservation.model;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemovingReservationRequest {

    public static Try<Result> remove(
            ReservationRequestsTimeSlot reservationRequestsTimeSlot,
            ReservationRequester requester,
            ReservationRequestId reservationRequestId
    ) {
        var timeSlotEventTry = reservationRequestsTimeSlot.remove(reservationRequestId);
        if (timeSlotEventTry.isFailure()) {
            return Try.failure(timeSlotEventTry.getCause());
        }
        var timeSlotEvent = timeSlotEventTry.get();

        var requesterEventTry = requester.remove(reservationRequestId);
        if (requesterEventTry.isFailure()) {
            return Try.failure(requesterEventTry.getCause());
        }
        var requesterEvent = requesterEventTry.get();

        return Try.of(() -> new Result(timeSlotEvent, requesterEvent));
    }

    public record Result(
            ReservationRequestsTimeSlotEvent.ReservationRequestRemoved timeSlotEvent,
            ReservationRequesterEvent.ReservationRequestRemoved requesterEvent
    ) {
    }

}
