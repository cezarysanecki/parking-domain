package pl.cezarysanecki.parkingdomain.requestingreservation.model;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemovingReservationRequest {

    public static Try<ReservationRequest> remove(
            ReservationRequestsTimeSlot reservationRequestsTimeSlot,
            ReservationRequester requester,
            ReservationRequestId reservationRequestId
    ) {
        Try<ReservationRequest> reservationRequestRemoval = reservationRequestsTimeSlot.remove(reservationRequestId);
        if (reservationRequestRemoval.isFailure()) {
            return Try.failure(reservationRequestRemoval.getCause());
        }

        Try<ReservationRequestId> requesterRemoval = requester.remove(reservationRequestId);
        if (requesterRemoval.isFailure()) {
            return Try.failure(requesterRemoval.getCause());
        }

        return Try.of(reservationRequestRemoval::get);
    }

}
