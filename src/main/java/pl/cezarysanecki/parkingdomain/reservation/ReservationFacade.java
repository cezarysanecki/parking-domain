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
  public <R> Optional<R> useReservationFor(ReservationId reservationId, Function<ReservedSpace, Optional<R>> useCase) {
    Reservation reservation = reservationRepository.loadActiveBy(reservationId);
    log.debug("found valid reservation with id {}", reservationId);

    Optional<R> result = useCase.apply(reservation);
    if (result.isEmpty()) {
      log.debug("reservation with id {} stays active, because it was not used", reservationId);
      return Optional.empty();
    }

    reservationRepository.markAsUsed(reservation);

    eventPublisher.publish(new ReservationUsed(reservationId));
    return result;
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

    reservationRepository.markAsActive(reservations.stream()
        .map(Reservation::reservationId)
        .toList());
  }

  @Transactional
  public void removeNotUsedReservationsFor(Instant date) {
    List<Reservation> reservations = reservationRepository.loadAllActiveSince(date);
    ReservationsRemoved event = new ReservationsRemoved(reservations.stream()
        .map(reservation -> new ReservationsRemoved.Entry(
            reservation.reservationId(),
            reservation.ownerId()))
        .toList());
    log.debug("removing {} not used reservations", event.reservations().size());

    eventPublisher.publish(event);

    reservationRepository.markAsNotUsed(event.reservationIds());
  }

}
