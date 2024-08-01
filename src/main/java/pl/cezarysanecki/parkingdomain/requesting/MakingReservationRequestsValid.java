package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
class MakingReservationRequestsValid {

  private final ReservationRequestRepository reservationRequestRepository;
  private final ReservationRequestsTimeSlotRepository reservationRequestsTimeSlotRepository;

  void makeAllValidSince(Instant date) {
    List<ReservationRequest> reservationRequests = reservationRequestRepository.findAllValidSince(date);
    log.debug("found {} reservation requests to make them valid", reservationRequests.size());

    var results = reservationRequests.map(ReservationRequest::confirm);

    results.forEach(reservationRequestRepository::publish);

    reservationRequestsTimeSlotRepository.removeAllValidSince(date);
    log.debug("removed time slots which were valid since {}", date);
  }

}
