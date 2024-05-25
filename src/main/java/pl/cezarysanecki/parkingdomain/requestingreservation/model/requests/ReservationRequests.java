package pl.cezarysanecki.parkingdomain.requestingreservation.model.requests;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsEvent.ReservationRequestMade;

@RequiredArgsConstructor
public class ReservationRequests {

  @NonNull
  private final ReservationRequestsTimeSlot timeSlot;
  @NonNull
  private final ReservationRequester requester;

  public Try<ReservationRequestMade> makeRequest(SpotUnits spotUnits) {
    ReservationRequest reservationRequest = ReservationRequest.newOne(
        requester.getRequesterId(),
        timeSlot.getTimeSlotId(),
        spotUnits);

    var timeSlotResult = timeSlot.append(reservationRequest);
    if (timeSlotResult.isFailure()) {
      return Try.failure(timeSlotResult.getCause());
    }

    var requesterResult = requester.append(reservationRequest.reservationRequestId());
    if (requesterResult.isFailure()) {
      return Try.failure(requesterResult.getCause());
    }

    return Try.of(() -> new ReservationRequestMade(
        requesterResult.get(),
        timeSlotResult.get()));
  }

  public Try<ReservationRequestCancelled> cancel(ReservationRequestId reservationRequestId) {
    var timeSlotResult = timeSlot.remove(reservationRequestId);
    if (timeSlotResult.isFailure()) {
      return Try.failure(timeSlotResult.getCause());
    }

    var requesterResult = requester.remove(reservationRequestId);
    if (requesterResult.isFailure()) {
      return Try.failure(requesterResult.getCause());
    }

    return Try.of(() -> new ReservationRequestCancelled(
        requesterResult.get(),
        timeSlotResult.get()));
  }

}
