package pl.cezarysanecki.parkingdomain.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationUsed;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationsActivated;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservedSpace;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class ReservationFacade {

  private final ReservationRepository reservationRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public <R> Optional<R> useReservationFor(ReservationId reservationId, Function<ReservedSpace, R> useCase) {
    Reservation reservation = reservationRepository.loadActiveBy(reservationId);

    R result = useCase.apply(reservation);

    reservationRepository.markAsUsed(reservation);

    eventPublisher.publish(new ReservationUsed(reservationId));
    return Optional.of(result);
  }

  @Transactional
  public void activateReservationsFor(Instant date) {
    List<Reservation> reservations = reservationRepository.loadAllStaleSince(date);
    List<ReservationsActivated.Entry> activatedReservationEntries = reservations.stream()
        .map(reservation -> new ReservationsActivated.Entry(
            reservation.reservationId(),
            reservation.parkingSpotId(),
            reservation.spotUnits()
        ))
        .toList();

    eventPublisher.publish(new ReservationsActivated(activatedReservationEntries));

    reservationRepository.markAsActive(activatedReservationEntries.stream()
        .map(ReservationsActivated.Entry::reservationId)
        .toList());
  }

  @Transactional
  public void removeNotUsedReservationsFor(Instant date) {
    List<Reservation> reservations = reservationRepository.loadAllActiveBy(date);
    List<ReservationId> activatedReservationEntries = reservations.stream()
        .map(Reservation::reservationId)
        .toList();

    eventPublisher.publish(new ReservationsRemoved(activatedReservationEntries));

    reservationRepository.markAsNotUused(activatedReservationEntries);
  }

}
