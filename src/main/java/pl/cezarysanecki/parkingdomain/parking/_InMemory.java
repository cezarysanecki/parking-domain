package pl.cezarysanecki.parkingdomain.parking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupantEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotSectionEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPANT_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPATION_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.PARKING_SPOT_SECTION_DATABASE;

@RequiredArgsConstructor
class InMemoryOccupationRepository implements OccupationRepository {

  private static final Map<OccupationId, OccupationEntity> DATABASE = OCCUPATION_DATABASE;

  @Override
  public void saveCheckingVersion(Occupation occupation) {
    DATABASE.put(occupation.occupationId(), new OccupationEntity(
        occupation.occupationId(),
        occupation.occupantId(),
        occupation.parkingSpotId(),
        occupation.sections().stream().map(ParkingSpotSection::sectionId).toList()
    ));
  }

  @Override
  public Optional<Occupation> delete(OccupationId occupationId) {
    return Optional.ofNullable(DATABASE.remove(occupationId))
        .map(removed -> toDomain(
            removed,
            InMemoryParkingRepository.findBy(removed.parkingSpotId))
        );
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

  private static Occupation toDomain(OccupationEntity entity, List<ParkingSpotSection> sections) {
    return new Occupation(
        entity.occupationId,
        entity.occupantId,
        entity.parkingSpotId,
        sections);
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
  public ParkingSpotSectionsGrouped loadFreeSectionsFor(ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    List<ParkingSpotSection> sections = DATABASE.values()
        .stream()
        .filter(section -> section.parkingSpotId.equals(parkingSpotId)
            && InMemoryOccupationRepository.findFor(section.sectionId).isEmpty())
        .limit(spotUnits.value())
        .map(entity -> toDomain(
            entity,
            InMemoryOccupationRepository.findFor(entity.sectionId).orElse(null))
        )
        .toList();
    if (sections.isEmpty()) {
      throw new EntityNotFoundException("cannot find free sections in parking spot with id " + parkingSpotId);
    }
    return new ParkingSpotSectionsGrouped(sections);
  }

  @Override
  public ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.parkingSpotId.equals(parkingSpotId))
            .map(entity -> toDomain(
                entity,
                InMemoryOccupationRepository.findFor(entity.sectionId).orElse(null))
            )
            .toList());
  }

  static List<ParkingSpotSection> findBy(ParkingSpotId parkingSpotId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .map(entity -> toDomain(
            entity,
            InMemoryOccupationRepository.findFor(entity.sectionId).orElse(null)
        ))
        .toList();
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
