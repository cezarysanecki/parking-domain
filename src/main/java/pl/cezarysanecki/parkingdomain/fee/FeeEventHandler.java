package pl.cezarysanecki.parkingdomain.fee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.ReservationsRemoved;

@Slf4j
@RequiredArgsConstructor
class FeeEventHandler {

  private final FeeFacade feeFacade;

  @Transactional
  @EventListener
  public void handle(ReservationsRemoved event) {
    event.reservations()
        .forEach(reservation -> feeFacade.chargeForNotUsedReservation(
            new ClientId(reservation.ownerId().value()),
            reservation.reservationId()));
  }

}
