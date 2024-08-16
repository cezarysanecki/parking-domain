package pl.cezarysanecki.parkingdomain.reservation;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.api.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationUsed;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ReservationFacade {

  private final ReservationRepository reservationRepository;
  private final EventPublisher eventPublisher;

  public void useReservationFor(ReservationId reservationId, Consumer<Reservation> useCase) {
    Reservation reservation = reservationRepository.loadBy(reservationId);
    useCase.accept(reservation);
    reservationRepository.saveCheckingUsage(reservation);

    eventPublisher.publish(new ReservationUsed(reservationId));
  }
}
