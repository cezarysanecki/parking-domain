package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsRepository;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

  private final ReservationRequestsRepository reservationRequestsRepository;

  public Try<ReservationRequest> cancelRequest(ReservationRequestId reservationRequestId) {
    ReservationRequests reservationRequests = reservationRequestsRepository.getBy(reservationRequestId);

    Try<ReservationRequestCancelled> result = reservationRequests.cancel(reservationRequestId);

    return result
        .onFailure(exception -> log.error("cannot cancel reservation request, reason: {}", exception.getMessage()))
        .onSuccess(event -> {
          log.debug("successfully cancelled reservation request with id {}", reservationRequestId);
          reservationRequestsRepository.publish(event);
        })
        .map(ReservationRequestCancelled::reservationRequest);
  }

}
