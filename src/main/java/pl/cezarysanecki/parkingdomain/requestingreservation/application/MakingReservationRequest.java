package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.ReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.ReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.makingrequest.ReservationRequestsEvent.ReservationRequestMade;

@Slf4j
@RequiredArgsConstructor
public class MakingReservationRequest {

  private final ReservationRequestsRepository reservationRequestsRepository;

  public Try<ReservationRequest> makeRequest(
      ReservationRequesterId requesterId,
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
