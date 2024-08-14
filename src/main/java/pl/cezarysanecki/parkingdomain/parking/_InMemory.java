package pl.cezarysanecki.parkingdomain.parking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReservationId;

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

  static Optional<OccupationId> findFor(ParkingSpotSectionId sectionId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.sections.stream()
            .anyMatch(section -> section.equals(sectionId)))
        .map(entity -> entity.occupationId)
        .findFirst();
  }

  static Optional<OccupationId> findFor(OccupantId occupantId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.occupantId.equals(occupantId))
        .map(entity -> entity.occupationId)
        .findFirst();
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
            .map(entity -> toDomain(
                entity,
                InMemoryOccupationRepository.findFor(entity.sectionId).orElse(null))
            )
            .toList(),
        InMemoryReservationRepository.findFor(parkingSpotId, activationDateOfReservations));
  }

  @Override
  public ParkingSpotSectionsGrouped loadBy(ReservationId reservationId, Instant activationDateOfReservations) {
    Optional<Reservation> reservation = InMemoryReservationRepository.tryFindBy(reservationId, activationDateOfReservations);
    if (reservation.isEmpty()) {
      throw new EntityNotFoundException("Not found reservation with id " + reservationId);
    }
    ParkingSpotId parkingSpotId = reservation.get().parkingSpotId();

    return loadBy(parkingSpotId, activationDateOfReservations);
  }

  private static ParkingSpotSection toDomain(ParkingSpotSectionEntity entity, OccupationId occupationId) {
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
  public Occupant findBy(OccupantId occupantId) {
    OccupantEntity entity = DATABASE.get(occupantId);
    if (entity == null) {
      throw new EntityNotFoundException("No occupant found with id " + occupantId);
    }
    return toDomain(
        entity,
        InMemoryOccupationRepository.findFor(occupantId).orElse(null)
    );
  }

  @Override
  public void saveNew(Occupant occupant) {
    DATABASE.put(occupant.occupantId(), new OccupantEntity(
        occupant.occupantId(),
        occupant.version().getVersion()
    ));
  }

  private static Occupant toDomain(OccupantEntity entity, OccupationId occupationId) {
    return new Occupant(
        entity.occupantId,
        occupationId,
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

  static Optional<Reservation> tryFindBy(ReservationId reservationId, Instant activationDateOfReservations) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.reservationId.equals(reservationId)
            && entity.timeSlot.from().isBefore(activationDateOfReservations))
        .findFirst()
        .map(entity -> new Reservation(
            entity.reservationId,
            entity.ownerId,
            entity.parkingSpotId,
            entity.timeSlot,
            entity.spotUnits
        ));
  }

  static List<Reservation> findFor(ParkingSpotId parkingSpotId, Instant activationDateOfReservations) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId)
            && entity.timeSlot.from().isBefore(activationDateOfReservations))
        .map(entity -> new Reservation(
            entity.reservationId,
            entity.ownerId,
            entity.parkingSpotId,
            entity.timeSlot,
            entity.spotUnits
        ))
        .toList();
  }

  static void deleteBy(ReservationId reservationId) {
    DATABASE.remove(reservationId);
  }

}
