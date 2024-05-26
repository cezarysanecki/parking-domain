package pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.ReservationRequestsEvent.ReservationRequestMade;

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
