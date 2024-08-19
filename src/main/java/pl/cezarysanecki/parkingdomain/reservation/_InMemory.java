package pl.cezarysanecki.parkingdomain.reservation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ReservationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.RESERVATION_DATABASE;

@RequiredArgsConstructor
class InMemoryReservationRepository implements ReservationRepository {

  private static final Map<ReservationId, ReservationEntity> DATABASE = RESERVATION_DATABASE;

  @Override
  public void saveAll(List<Reservation> reservations) {
    reservations.forEach(
        reservation -> DATABASE.put(reservation.reservationId(), new ReservationEntity(
            reservation.reservationId(),
            reservation.ownerId(),
            reservation.parkingSpotId(),
            reservation.timeSlot(),
            reservation.spotUnits(),
            ReservationEntity.Status.STALE)
        ));
  }

  @Override
  public Reservation loadActiveBy(ReservationId reservationId) {
    return loadBy(
        reservationEntity -> reservationEntity.reservationId.equals(reservationId)
            && reservationEntity.status == ReservationEntity.Status.ACTIVE
    )
        .findFirst()
        .map(InMemoryReservationRepository::toDomain)
        .orElseThrow(() -> new EntityNotFoundException("cannot find reservation with id " + reservationId));
  }

  @Override
  public List<Reservation> loadAllStaleSince(Instant date) {
    return loadBy(
        reservationEntity -> reservationEntity.timeSlot.from().isBefore(date)
            && reservationEntity.status == ReservationEntity.Status.STALE
    )
        .map(InMemoryReservationRepository::toDomain)
        .toList();
  }

  @Override
  public List<Reservation> loadAllActiveBy(Instant date) {
    return loadBy(reservationEntity -> reservationEntity.timeSlot.from().isBefore(date)
        && reservationEntity.status == ReservationEntity.Status.ACTIVE
    )
        .map(InMemoryReservationRepository::toDomain)
        .toList();
  }

  @Override
  public void markAsUsed(Reservation reservation) {
    loadBy(
        reservationEntity -> reservationEntity.reservationId.equals(reservation.reservationId())
    )
        .findFirst()
        .ifPresentOrElse(
            reservationEntity -> reservationEntity.status = ReservationEntity.Status.USED,
            () -> {
              throw new EntityNotFoundException("cannot find reservation with id " + reservation.reservationId());
            }
        );
  }

  @Override
  public void markAsActive(List<ReservationId> reservations) {
    loadBy(
        reservationEntity -> reservations.contains(reservationEntity.reservationId)
    )
        .forEach(reservationEntity -> reservationEntity.status = ReservationEntity.Status.ACTIVE);
  }

  @Override
  public void markAsNotUsed(List<ReservationId> reservations) {
    loadBy(
        reservationEntity -> reservations.contains(reservationEntity.reservationId)
    )
        .forEach(reservationEntity -> reservationEntity.status = ReservationEntity.Status.NOT_USED);
  }

  private static Stream<ReservationEntity> loadBy(Predicate<ReservationEntity> condition) {
    return DATABASE.values()
        .stream()
        .filter(condition);
  }

  static Reservation toDomain(ReservationEntity entity) {
    return new Reservation(
        entity.reservationId,
        entity.ownerId,
        entity.parkingSpotId,
        entity.timeSlot,
        entity.spotUnits);
  }

}
