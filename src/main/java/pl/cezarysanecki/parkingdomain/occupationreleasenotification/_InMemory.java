package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class InMemoryOccupationReleaseNotificationRepository implements OccupationReleaseNotificationRepository {

  private static final Map<ParkingSpotId, ParkingSpotCapacity> PARKING_SPOT_DATABASE = new ConcurrentHashMap<>();
  private static final Map<OccupationId, OccupationEntity> OCCUPATION_DATABASE = new ConcurrentHashMap<>();
  private static final Map<ReservationId, ReservationEntity> RESERVATION_DATABASE = new ConcurrentHashMap<>();
  private static final List<Instant> DONE_NOTIFICATION_DATABASE = new ArrayList<>();

  @Override
  public void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
    PARKING_SPOT_DATABASE.put(parkingSpotId, capacity);
  }

  @Override
  public void addOccupation(OccupationId occupationId, OccupantId occupantId, ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    OCCUPATION_DATABASE.put(occupationId, new OccupationEntity(
        occupationId, occupantId, parkingSpotId, spotUnits
    ));
  }

  @Override
  public void removeOccupation(OccupationId occupationId) {
    OCCUPATION_DATABASE.remove(occupationId);
  }

  @Override
  public void saveReservation(ReservationId reservationId, ReservationOwnerId reservationOwnerId, ParkingSpotId parkingSpotId, Instant validSince, SpotUnits spotUnits) {
    RESERVATION_DATABASE.put(reservationId, new ReservationEntity(
        reservationId, reservationOwnerId, parkingSpotId, spotUnits, validSince
    ));
  }

  @Override
  public void doneFor(Instant date) {
    DONE_NOTIFICATION_DATABASE.add(date);
  }

  @Override
  public List<NotificationResolver> findFor(Instant date) {
    Instant latestDone = latest();

    Set<Key> keys = RESERVATION_DATABASE.values()
        .stream()
        .filter(reservationEntity -> reservationEntity.validSince.isBefore(date) && reservationEntity.validSince.isAfter(latestDone))
        .map(reservationEntity -> new Key(reservationEntity.parkingSpotId, reservationEntity.validSince))
        .collect(Collectors.toSet());

    return keys.stream()
        .map(key -> {
          ParkingSpotCapacity capacity = PARKING_SPOT_DATABASE.get(key.parkingSpotId);

          List<NotificationResolver.Occupation> occupations = OCCUPATION_DATABASE.values()
              .stream()
              .filter(occupationEntity -> occupationEntity.parkingSpotId.equals(key.parkingSpotId))
              .map(occupationEntity -> new NotificationResolver.Occupation(
                  occupationEntity.occupationId, occupationEntity.occupantId, occupationEntity.spotUnits
              ))
              .toList();
          List<NotificationResolver.Reservation> reservations = RESERVATION_DATABASE.values()
              .stream()
              .filter(reservationEntity -> reservationEntity.parkingSpotId.equals(key.parkingSpotId))
              .map(reservationEntity -> new NotificationResolver.Reservation(
                  reservationEntity.reservationId, reservationEntity.reservationOwnerId, reservationEntity.spotUnits
              ))
              .toList();

          return new NotificationResolver(
              key.validSince,
              key.parkingSpotId,
              capacity,
              occupations,
              reservations);
        })
        .toList();
  }

  private Instant latest() {
    return DONE_NOTIFICATION_DATABASE.stream()
        .max(Instant::compareTo)
        .orElse(Instant.MIN);
  }

  private record Key(
      ParkingSpotId parkingSpotId,
      Instant validSince
  ) {
  }

  private record OccupationEntity(
      OccupationId occupationId,
      OccupantId occupantId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits
  ) {
  }

  private record ReservationEntity(
      ReservationId reservationId,
      ReservationOwnerId reservationOwnerId,
      ParkingSpotId parkingSpotId,
      SpotUnits spotUnits,
      Instant validSince) {
  }

}
