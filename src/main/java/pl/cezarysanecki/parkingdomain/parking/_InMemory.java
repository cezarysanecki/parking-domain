package pl.cezarysanecki.parkingdomain.parking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReleasedOccupation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupantEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotReservationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPANT_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPATION_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.PARKING_DATABASE;

@RequiredArgsConstructor
class InMemoryOccupationRepository implements OccupationRepository {

  private static final Map<OccupationId, OccupationEntity> DATABASE = OCCUPATION_DATABASE;

  @Override
  public void saveCheckingVersion(Occupation occupation) {
    DATABASE.put(occupation.occupationId(), new OccupationEntity(
        occupation.occupationId(),
        occupation.occupant().occupantId(),
        occupation.parkingSpot().parkingSpotId(),
        occupation.occupiedUnits()
    ));
  }

  @Override
  public Optional<ReleasedOccupation> delete(OccupationId occupationId) {
    return Optional.ofNullable(DATABASE.remove(occupationId))
        .map(removed -> new ReleasedOccupation(
            removed.occupationId,
            removed.occupantId,
            removed.parkingSpotId,
            removed.occupiedSpace
        ));
  }

  static Optional<OccupationEntity> findFor(ParkingSpotId parkingSpotId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .findFirst();
  }

  static List<OccupationEntity> findFor(OccupantId occupantId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.occupantId.equals(occupantId))
        .toList();
  }

}

@RequiredArgsConstructor
class InMemoryParkingRepository implements ParkingRepository {

  private static final Map<ParkingSpotId, ParkingSpotEntity> DATABASE = PARKING_DATABASE;

  @Override
  public void saveNew(ParkingSpot parkingSpot) {
    DATABASE.put(parkingSpot.parkingSpotId(), new ParkingSpotEntity(
        parkingSpot.parkingSpotId(),
        parkingSpot.capacity(),
        parkingSpot.version().getVersion()
    ));
  }

  @Override
  public ParkingSpot loadBy(ParkingSpotId parkingSpotId) {
    ParkingSpotEntity parkingSpot = DATABASE.get(parkingSpotId);
    if (parkingSpot == null) {
      throw new IllegalArgumentException("No parking spot with id " + parkingSpotId + " found");
    }
    return toDomain(parkingSpot);
  }

  private static ParkingSpot toDomain(ParkingSpotEntity entity) {
    Optional<OccupationEntity> occupations = InMemoryOccupationRepository.findFor(entity.parkingSpotId);
    List<ParkingSpotReservationEntity> reservations = InMemoryActiveReservationRepository.findFor(entity.parkingSpotId);
    return new ParkingSpot(
        entity.parkingSpotId,
        occupations.stream()
            .map(occupationEntity -> occupationEntity.occupiedSpace)
            .map(SpotUnits::value)
            .reduce(0, Integer::sum),
        reservations.stream()
            .map(ParkingSpotReservationEntity::spotUnits)
            .map(SpotUnits::value)
            .reduce(0, Integer::sum),
        entity.capacity,
        new Version(entity.version));
  }

}

@RequiredArgsConstructor
class InMemoryOccupantRepository implements OccupantRepository {

  private static final Map<OccupantId, OccupantEntity> DATABASE = OCCUPANT_DATABASE;

  @Override
  public Occupant findBy(OccupantId occupantId) {
    OccupantEntity entity = DATABASE.get(occupantId);
    if (entity == null) {
      throw new EntityNotFoundException("No occupant found with id " + occupantId);
    }
    return toDomain(entity);
  }

  @Override
  public void saveNew(Occupant occupant) {
    DATABASE.put(occupant.occupantId(), new OccupantEntity(
        occupant.occupantId(),
        occupant.version().getVersion()
    ));
  }

  private static Occupant toDomain(OccupantEntity entity) {
    return new Occupant(
        entity.occupantId,
        InMemoryOccupationRepository.findFor(entity.occupantId)
            .stream()
            .map(occupationEntity -> occupationEntity.occupationId)
            .toList(),
        new Version(entity.version)
    );
  }

}

@Slf4j
@RequiredArgsConstructor
class InMemoryActiveReservationRepository implements ActiveReservationRepository {

  static final Map<ReservationId, ParkingSpotReservationEntity> DATABASE = InMemoryRepositories.PARKING_SPOT_RESERVATION_DATABASE;

  @Override
  public void storeFor(ParkingSpotId parkingSpotId, ReservationId reservationId, ReservationOwnerId reservationOwnerId, SpotUnits spotUnits) {
    if (InMemoryOccupationRepository.findFor(new OccupantId(reservationOwnerId.value()))
        .stream()
        .anyMatch(occupationEntity -> occupationEntity.parkingSpotId.equals(parkingSpotId))) {
      log.debug("reservation owner with id {} has already occupation for parking spot with id {}", reservationOwnerId, parkingSpotId);
      return;
    }

    DATABASE.put(reservationId, new ParkingSpotReservationEntity(
        parkingSpotId,
        reservationId,
        spotUnits
    ));
  }

  @Override
  public void remove(List<ReservationId> reservations) {
    reservations.forEach(DATABASE::remove);
  }

  static List<ParkingSpotReservationEntity> findFor(ParkingSpotId parkingSpotId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId().equals(parkingSpotId))
        .toList();
  }


}
