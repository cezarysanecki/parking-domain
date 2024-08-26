package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.NotificationRunsRecord;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.NotificationOccupation.NOTIFICATION_OCCUPATION;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.NotificationParkingSpot.NOTIFICATION_PARKING_SPOT;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.NotificationReservation.NOTIFICATION_RESERVATION;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.NotificationRuns.NOTIFICATION_RUNS;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdOccupationReleaseNotificationRepository implements OccupationReleaseNotificationRepository {

  private final DSLContext create;

  @Override
  public void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
    create.insertInto(NOTIFICATION_PARKING_SPOT)
        .set(NOTIFICATION_PARKING_SPOT.PARKING_SPOT, parkingSpotId.value())
        .set(NOTIFICATION_PARKING_SPOT.CAPACITY, capacity.value())
        .execute();
  }

  @Override
  public void addOccupation(OccupationId occupationId, OccupantId occupantId, ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    create.insertInto(NOTIFICATION_OCCUPATION)
        .set(NOTIFICATION_OCCUPATION.OCCUPATION, occupationId.value())
        .set(NOTIFICATION_OCCUPATION.OCCUPANT, occupantId.value())
        .set(NOTIFICATION_OCCUPATION.PARKING_SPOT, parkingSpotId.value())
        .set(NOTIFICATION_OCCUPATION.SPOT_UNITS, spotUnits.value())
        .execute();
  }

  @Override
  public void removeOccupation(OccupationId occupationId) {
    create.deleteFrom(NOTIFICATION_OCCUPATION)
        .where(NOTIFICATION_OCCUPATION.OCCUPATION.eq(occupationId.value()))
        .execute();
  }

  @Override
  public void saveReservation(ReservationId reservationId, ReservationOwnerId reservationOwnerId, ParkingSpotId parkingSpotId, Instant startDate, SpotUnits spotUnits) {
    create.insertInto(NOTIFICATION_RESERVATION)
        .set(NOTIFICATION_RESERVATION.RESERVATION, reservationId.value())
        .set(NOTIFICATION_RESERVATION.RESERVATION_OWNER, reservationOwnerId.value())
        .set(NOTIFICATION_RESERVATION.PARKING_SPOT, parkingSpotId.value())
        .set(NOTIFICATION_RESERVATION.VALID_SINCE, LocalDateTime.ofInstant(startDate, ZoneId.systemDefault()))
        .set(NOTIFICATION_RESERVATION.SPOT_UNITS, spotUnits.value())
        .execute();
  }

  @Override
  public void doneFor(Instant date) {
    create.insertInto(NOTIFICATION_RUNS)
        .set(NOTIFICATION_RUNS.DATE, LocalDateTime.ofInstant(date, ZoneId.systemDefault()))
        .execute();
  }

  @Override
  public List<NotificationResolver> findFor(Instant date) {
    LocalDateTime latestRun = create.selectFrom(NOTIFICATION_RUNS)
        .orderBy(NOTIFICATION_RUNS.DATE.desc())
        .fetchOptional()
        .map(NotificationRunsRecord::getDate)
        .orElse(LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()));

    var parkingSpots = create
        .select(
            NOTIFICATION_RESERVATION.PARKING_SPOT,
            NOTIFICATION_PARKING_SPOT.CAPACITY,
            NOTIFICATION_RESERVATION.VALID_SINCE)
        .distinctOn(NOTIFICATION_RESERVATION.PARKING_SPOT)
        .from(NOTIFICATION_RESERVATION)
        .join(NOTIFICATION_PARKING_SPOT)
        .on(NOTIFICATION_RESERVATION.PARKING_SPOT.eq(NOTIFICATION_PARKING_SPOT.PARKING_SPOT))
        .where(NOTIFICATION_RESERVATION.VALID_SINCE.between(
            latestRun, LocalDateTime.ofInstant(date, ZoneId.systemDefault())
        ))
        .fetch();

    return parkingSpots.stream()
        .map(record -> {
          UUID parkingSpotId = record.get(NOTIFICATION_PARKING_SPOT.PARKING_SPOT);
          Integer capacity = record.get(NOTIFICATION_PARKING_SPOT.CAPACITY);
          LocalDateTime validSince = record.get(NOTIFICATION_RESERVATION.VALID_SINCE);

          List<NotificationResolver.Reservation> reservations = create
              .selectFrom(NOTIFICATION_RESERVATION)
              .where(NOTIFICATION_RESERVATION.PARKING_SPOT.eq(parkingSpotId))
              .fetch()
              .map(reservationRecord -> new NotificationResolver.Reservation(
                  new ReservationId(reservationRecord.getReservation()),
                  new ReservationOwnerId(reservationRecord.getReservationOwner()),
                  new SpotUnits(reservationRecord.getSpotUnits())
              ))
              .stream().toList();
          List<NotificationResolver.Occupation> occupations = create
              .selectFrom(NOTIFICATION_OCCUPATION)
              .where(NOTIFICATION_OCCUPATION.PARKING_SPOT.eq(parkingSpotId))
              .fetch()
              .map(occupationRecord -> new NotificationResolver.Occupation(
                  new OccupationId(occupationRecord.getOccupation()),
                  new OccupantId(occupationRecord.getOccupant()),
                  new SpotUnits(occupationRecord.getSpotUnits())
              ))
              .stream().toList();

          return new NotificationResolver(
              validSince.atZone(ZoneId.systemDefault()).toInstant(),
              new ParkingSpotId(parkingSpotId),
              new ParkingSpotCapacity(capacity),
              occupations,
              reservations);
        })
        .toList();
  }

}
