package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.OccupationEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.ParkingSpotSectionEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.OCCUPATION_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.PARKING_SPOT_SECTION_DATABASE;

@RequiredArgsConstructor
class InMemoryOccupationRepository implements OccupationRepository {

  private static final Map<OccupationId, OccupationEntity> DATABASE = OCCUPATION_DATABASE;

  @Override
  public void saveCheckingVersion(Occupation occupation) {
    DATABASE.put(occupation.occupationId(), new OccupationEntity(
        occupation.occupationId(),
        occupation.occupant(),
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

  private static Occupation toDomain(OccupationEntity entity, List<ParkingSpotSection> sections) {
    return new Occupation(
        entity.occupationId,
        entity.occupant,
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
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.parkingSpotId.equals(parkingSpotId)
                && InMemoryOccupationRepository.findFor(section.sectionId).isEmpty())
            .limit(spotUnits.value())
            .map(entity -> toDomain(
                entity,
                InMemoryOccupationRepository.findFor(entity.sectionId).orElse(null))
            )
            .toList());
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
