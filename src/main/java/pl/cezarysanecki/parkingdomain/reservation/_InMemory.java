package pl.cezarysanecki.parkingdomain.reservation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.reservation.api.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.util.List;
import java.util.Map;

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
            reservation.spotUnits()
        ))
    );
  }

  @Override
  public Reservation loadBy(ReservationId reservationId) {
    ReservationEntity entity = DATABASE.get(reservationId);
    if (entity == null) {
      throw new EntityNotFoundException("No reservation found with id " + reservationId);
    }
    return toDomain(entity);
  }

  @Override
  public void saveCheckingUsage(Reservation reservation) {
    DATABASE.put(reservation.reservationId(), new ReservationEntity(
        reservation.reservationId(),
        reservation.ownerId(),
        reservation.parkingSpotId(),
        reservation.timeSlot(),
        reservation.spotUnits()
    ));
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
