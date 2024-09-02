package pl.cezarysanecki.parkingdomain.reservation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.jooq.tables.records.ReservationRecord;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.jooq.impl.DSL.row;
import static pl.cezarysanecki.parkingdomain.jooq.tables.Reservation.RESERVATION;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdReservationRepository implements ReservationRepository {

  public enum Status {
    STALE, ACTIVE, USED, NOT_USED
  }

  private final DSLContext create;

  @Override
  public void saveAll(List<Reservation> reservations) {
    var listOfValues = reservations.stream()
        .map(r -> row(
            r.reservationId().value(),
            r.ownerId().value(),
            r.parkingSpotId().value(),
            LocalDateTime.ofInstant(r.timeSlot().from(), ZoneId.systemDefault()),
            LocalDateTime.ofInstant(r.timeSlot().to(), ZoneId.systemDefault()),
            r.spotUnits().value(),
            Status.STALE.name()))
        .toList();

    create.insertInto(RESERVATION,
            RESERVATION.ID,
            RESERVATION.OWNER,
            RESERVATION.PARKING_SPOT,
            RESERVATION.FROM,
            RESERVATION.TO,
            RESERVATION.SPOT_UNITS,
            RESERVATION.STATUS)
        .valuesOfRows(listOfValues)
        .execute();
  }

  @Override
  public Reservation loadActiveBy(ReservationId reservationId) {
    ReservationRecord reservationRecord = create
        .selectFrom(RESERVATION)
        .where(RESERVATION.ID.eq(reservationId.value()))
        .and(RESERVATION.STATUS.eq(Status.ACTIVE.name()))
        .fetchOne();
    if (reservationRecord == null) {
      throw new EntityNotFoundException("cannot find reservation with id " + reservationId);
    }
    return new Reservation(
        new ReservationId(reservationRecord.getId()),
        new ReservationOwnerId(reservationRecord.getOwner()),
        new ParkingSpotId(reservationRecord.getParkingSpot()),
        new TimeSlot(
            reservationRecord.getFrom().atZone(ZoneId.systemDefault()).toInstant(),
            reservationRecord.getTo().atZone(ZoneId.systemDefault()).toInstant()
        ),
        new SpotUnits(reservationRecord.getSpotUnits())
    );
  }

  @Override
  public List<Reservation> loadAllStaleSince(Instant date) {
    return create
        .selectFrom(RESERVATION)
        .where(RESERVATION.STATUS.eq(Status.STALE.name()))
        .and(RESERVATION.FROM.le(LocalDateTime.ofInstant(date, ZoneId.systemDefault())))
        .fetch()
        .map(record -> new Reservation(
            new ReservationId(record.getId()),
            new ReservationOwnerId(record.getOwner()),
            new ParkingSpotId(record.getParkingSpot()),
            new TimeSlot(
                record.getFrom().atZone(ZoneId.systemDefault()).toInstant(),
                record.getTo().atZone(ZoneId.systemDefault()).toInstant()
            ),
            new SpotUnits(record.getSpotUnits())
        ))
        .stream().toList();
  }

  @Override
  public List<Reservation> loadAllActiveSince(Instant date) {
    return create
        .selectFrom(RESERVATION)
        .where(RESERVATION.STATUS.eq(Status.ACTIVE.name()))
        .and(RESERVATION.FROM.le(LocalDateTime.ofInstant(date, ZoneId.systemDefault())))
        .fetch()
        .map(record -> new Reservation(
            new ReservationId(record.getId()),
            new ReservationOwnerId(record.getOwner()),
            new ParkingSpotId(record.getParkingSpot()),
            new TimeSlot(
                record.getFrom().atZone(ZoneId.systemDefault()).toInstant(),
                record.getTo().atZone(ZoneId.systemDefault()).toInstant()
            ),
            new SpotUnits(record.getSpotUnits())
        ))
        .stream().toList();
  }

  @Override
  public void markAsUsed(Reservation reservation) {
    create
        .update(RESERVATION)
        .set(RESERVATION.STATUS, Status.USED.name())
        .where(RESERVATION.ID.eq(reservation.reservationId().value()))
        .execute();
  }

  @Override
  public void markAsActive(List<ReservationId> reservations) {
    if (reservations.isEmpty()) {
      return;
    }
    create
        .update(RESERVATION)
        .set(RESERVATION.STATUS, Status.ACTIVE.name())
        .where(RESERVATION.ID.in(reservations.stream().map(ReservationId::value).toList()))
        .execute();
  }

  @Override
  public void markAsNotUsed(List<ReservationId> reservations) {
    if (reservations.isEmpty()) {
      return;
    }
    create
        .update(RESERVATION)
        .set(RESERVATION.STATUS, Status.NOT_USED.name())
        .where(RESERVATION.ID.in(reservations.stream().map(ReservationId::value).toList()))
        .execute();
  }
}
