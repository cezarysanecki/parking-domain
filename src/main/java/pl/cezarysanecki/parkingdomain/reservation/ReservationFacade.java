package pl.cezarysanecki.parkingdomain.reservation;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationUsed;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservedSpace;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class ReservationFacade {

  private final ReservationRepository reservationRepository;
  private final EventPublisher eventPublisher;

  public <R> Optional<R> useReservationFor(ReservationId reservationId, Function<ReservedSpace, R> useCase) {
    Reservation reservation = reservationRepository.loadBy(reservationId);
    if (reservation.used) {
      return Optional.empty();
    }

    R result = useCase.apply(reservation);

    reservation.used = true;
    reservationRepository.save(reservation);

    eventPublisher.publish(new ReservationUsed(reservationId));
    return Optional.of(result);
  }

}
