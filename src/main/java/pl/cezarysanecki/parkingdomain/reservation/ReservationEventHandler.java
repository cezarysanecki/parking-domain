package pl.cezarysanecki.parkingdomain.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.cezarysanecki.parkingdomain.requesting.api.MadeRequestsValid;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;

@Slf4j
@RequiredArgsConstructor
class ReservationEventHandler {

  private final ReservationRepository reservationRepository;

  @TransactionalEventListener
  public void handle(MadeRequestsValid event) {
    reservationRepository.saveAll(
        event.requests()
            .stream()
            .map(request -> new Reservation(
                new ReservationId(request.requestId().value()),
                new ReservationOwnerId(request.requester().value()),
                request.parkingSpotId(),
                request.timeSlot(),
                request.spotUnits()
            ))
            .toList());
  }

}
