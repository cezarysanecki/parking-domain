package pl.cezarysanecki.parkingdomain.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class ReservationFacade {

  private final EventPublisher eventPublisher;
  private final ReservationRepository reservationRepository;

  @Transactional
  public <R> Optional<R> useReservationFor(ReservationId reservationId, Function<ReservedSpace, R> useCase) {
    Reservation reservation = reservationRepository.loadActiveBy(reservationId);
    log.debug("found valid reservation with id {}", reservationId);

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
            reservation.ownerId(),
            reservation.parkingSpotId(),
            reservation.timeSlot().from(),
            reservation.spotUnits()
        ))
        .toList();
    log.debug("activating {} reservations", activatedReservationEntries.size());

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
    log.debug("removing {} not used reservations", activatedReservationEntries.size());

    eventPublisher.publish(new ReservationsRemoved(activatedReservationEntries));

    reservationRepository.markAsNotUsed(activatedReservationEntries);
  }

}
