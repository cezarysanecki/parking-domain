package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requesting.ReservationRequestsEvent.ReservationRequestMade;

@Slf4j
@RequiredArgsConstructor
class MakingReservationRequest {

  private final ReservationRequestsRepository reservationRequestsRepository;

  Try<ReservationRequest> makeRequest(
      RequesterId requesterId,
      ReservationRequestsTimeSlotId timeSlotId,
      SpotUnits spotUnits
  ) {
    log.debug("making reservation request for time slot with id {}", timeSlotId);

    return Try.of(() -> {
          ReservationRequests reservationRequests = reservationRequestsRepository.getBy(requesterId, timeSlotId);

          Try<ReservationRequestMade> result = reservationRequests.makeRequest(spotUnits);

          return result
              .onSuccess(event -> {
                reservationRequestsRepository.publish(event);
                log.debug("successfully made reservation request with id {}", event.reservationRequest().getReservationRequestId());
              })
              .map(ReservationRequestMade::reservationRequest);
        })
        .flatMap(result -> result)
        .onFailure(exception -> log.error("cannot make reservation request, reason: {}", exception.getMessage()));
  }

}
