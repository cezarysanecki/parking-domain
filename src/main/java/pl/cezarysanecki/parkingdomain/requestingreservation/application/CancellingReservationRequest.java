package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestRepository;

@Slf4j
@RequiredArgsConstructor
public class CancellingReservationRequest {

  private final ReservationRequestRepository reservationRequestRepository;

  public Try<ReservationRequestId> cancelRequest(ReservationRequestId reservationRequestId) {
    ReservationRequest reservationRequest = reservationRequestRepository.getBy(reservationRequestId);

    var result = reservationRequest.cancel();

    reservationRequestRepository.publish(result);

    return Try.of(reservationRequest::getReservationRequestId);
  }

}
