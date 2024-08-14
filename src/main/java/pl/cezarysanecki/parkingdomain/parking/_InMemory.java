package pl.cezarysanecki.parkingdomain.parking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupantEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotSectionEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ReservationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPANT_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPATION_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.PARKING_SPOT_SECTION_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.RESERVATION_DATABASE;

@RequiredArgsConstructor
class InMemoryOccupationRepository implements OccupationRepository {

  private static final Map<OccupationId, OccupationEntity> DATABASE = OCCUPATION_DATABASE;

  @Override
  public void saveCheckingVersion(Occupation occupation) {
    DATABASE.put(occupation.occupationId(), new OccupationEntity(
        occupation.occupationId(),
        occupation.occupant().occupantId(),
        occupation.parkingSpotSectionsGrouped().id(),
        occupation.parkingSpotSectionsGrouped().sections().stream().map(ParkingSpotSection::sectionId).toList(),
        Optional.ofNullable(occupation.reservationId())
    ));
    if (occupation.reservationId() != null) {
      InMemoryReservationRepository.deleteBy(occupation.reservationId());
    }
  }

  @Override
  public Optional<ReleasedOccupation> delete(OccupationId occupationId) {
    return Optional.ofNullable(DATABASE.remove(occupationId))
        .map(removed -> new ReleasedOccupation(
            removed.occupationId,
            removed.occupantId,
            removed.parkingSpotId,
            removed.sections
        ));
  }

  static Optional<OccupationEntity> findFor(ParkingSpotSectionId sectionId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.sections.stream()
            .anyMatch(section -> section.equals(sectionId)))
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

  private static final Map<ParkingSpotSectionId, ParkingSpotSectionEntity> DATABASE = PARKING_SPOT_SECTION_DATABASE;

  @Override
  public void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped) {
    parkingSpotSectionsGrouped.sections()
        .forEach(section -> DATABASE.put(
            section.sectionId(),
            new ParkingSpotSectionEntity(section.parkingSpotId(), section.sectionId(), 0)));
  }

  @Override
  public ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId, Instant activationDateOfReservations) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.parkingSpotId.equals(parkingSpotId))
            .map(InMemoryParkingRepository::toDomain)
            .toList(),
        InMemoryReservationRepository.findFor(parkingSpotId, activationDateOfReservations)
            .stream()
            .map(reservationEntity -> reservationEntity.spotUnits)
            .map(SpotUnits::value)
            .reduce(0, Integer::sum));
  }

  private static ParkingSpotSection toDomain(ParkingSpotSectionEntity entity) {
    OccupationId occupationId = InMemoryOccupationRepository.findFor(entity.sectionId)
        .map(occupationEntity -> occupationEntity.occupationId)
        .orElse(null);
    return new ParkingSpotSection(
        entity.parkingSpotId,
        entity.sectionId,
        occupationId,
        entity.version
    );
  }

}

@RequiredArgsConstructor
class InMemoryOccupantRepository implements OccupantRepository {

  private static final Map<OccupantId, OccupantEntity> DATABASE = OCCUPANT_DATABASE;

  @Override
  public Occupant findBy(OccupantId occupantId, Instant activationDateOfReservations) {
    OccupantEntity entity = DATABASE.get(occupantId);
    if (entity == null) {
      throw new EntityNotFoundException("No occupant found with id " + occupantId);
    }
    return toDomain(entity, activationDateOfReservations);
  }

  @Override
  public void saveNew(Occupant occupant) {
    DATABASE.put(occupant.occupantId(), new OccupantEntity(
        occupant.occupantId(),
        occupant.version().getVersion()
    ));
  }

  private static Occupant toDomain(OccupantEntity entity, Instant activationDateOfReservations) {
    return new Occupant(
        entity.occupantId,
        InMemoryOccupationRepository.findFor(entity.occupantId)
            .stream()
            .map(occupationEntity -> occupationEntity.occupationId)
            .toList(),
        InMemoryReservationRepository.findFor(entity.occupantId, activationDateOfReservations)
            .stream()
            .map(reservationEntity -> reservationEntity.reservationId)
            .toList(),
        new Version(entity.version)
    );
  }

}

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

  static List<ReservationEntity> findFor(ParkingSpotId parkingSpotId, Instant activationDateOfReservations) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId)
            && entity.timeSlot.from().isBefore(activationDateOfReservations))
        .toList();
  }

  static List<ReservationEntity> findFor(OccupantId occupantId, Instant activationDateOfReservations) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.ownerId.value().equals(occupantId.value())
            && entity.timeSlot.from().isBefore(activationDateOfReservations))
        .toList();
  }

  static void deleteBy(ReservationId reservationId) {
    DATABASE.remove(reservationId);
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
