package pl.cezarysanecki.parkingdomain.requestingreservation.model;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppendingReservationRequest {

    public static Try<Result> append(
            ReservationRequestsTimeSlot reservationRequestsTimeSlot,
            ReservationRequester requester,
            ReservationRequest reservationRequest
    ) {
        var timeSlotEventTry = reservationRequestsTimeSlot.append(reservationRequest);
        if (timeSlotEventTry.isFailure()) {
            return Try.failure(timeSlotEventTry.getCause());
        }
        var timeSlotEvent = timeSlotEventTry.get();

        var requesterEventTry = requester.append(reservationRequest.getReservationRequestId());
        if (requesterEventTry.isFailure()) {
            return Try.failure(requesterEventTry.getCause());
        }
        var requesterEvent = requesterEventTry.get();

        return Try.of(() -> new Result(timeSlotEvent, requesterEvent));
    }

    public record Result(
            ReservationRequestsTimeSlotEvent.ReservationRequestAppended timeSlotEvent,
            ReservationRequesterEvent.ReservationRequestAppended requesterEvent
    ) {
    }

}
