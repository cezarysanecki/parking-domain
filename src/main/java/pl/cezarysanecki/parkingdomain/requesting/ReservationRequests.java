package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsEvent.ReservationRequestMade;

@RequiredArgsConstructor
public class ReservationRequests {

  @NonNull
  private final ReservationRequestsTimeSlot timeSlot;
  @NonNull
  private final ReservationRequester requester;

  public Try<ReservationRequestMade> makeRequest(SpotUnits spotUnits) {
    ReservationRequest reservationRequest = new ReservationRequest(
        requester.requesterId(),
        timeSlot.timeSlotId(),
        spotUnits);

    var requesterResult = requester.append(reservationRequest.getReservationRequestId());
    if (requesterResult.isFailure()) {
      return Try.failure(requesterResult.getCause());
    }

    var timeSlotResult = timeSlot.append(spotUnits);
    if (timeSlotResult.isFailure()) {
      return Try.failure(timeSlotResult.getCause());
    }

    return Try.of(() -> new ReservationRequestMade(reservationRequest, requester.version(), timeSlot.version()));
  }

}
