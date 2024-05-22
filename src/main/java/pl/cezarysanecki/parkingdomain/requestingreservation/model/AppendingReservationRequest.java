package pl.cezarysanecki.parkingdomain.requestingreservation.model;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppendingReservationRequest {

    public static Try<ReservationRequest> append(
            ReservationRequestsTimeSlot reservationRequestsTimeSlot,
            ReservationRequester requester,
            SpotUnits spotUnits
    ) {
        Try<ReservationRequest> reservationRequestAppend = reservationRequestsTimeSlot.append(requester.getRequesterId(), spotUnits);
        if (reservationRequestAppend.isFailure()) {
            return Try.failure(reservationRequestAppend.getCause());
        }
        ReservationRequest reservationRequest = reservationRequestAppend.get();

        Try<ReservationRequestId> requesterAppend = requester.append(reservationRequest.getReservationRequestId());
        if (requesterAppend.isFailure()) {
            return Try.failure(requesterAppend.getCause());
        }

        return Try.of(() -> reservationRequest);
    }

}
