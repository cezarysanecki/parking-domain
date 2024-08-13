package pl.cezarysanecki.parkingdomain.parking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryOccupationRepository implements OccupationRepository {

  private static final Map<OccupationId, OccupationEntity> DATABASE = new ConcurrentHashMap<>();

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
        .map(removed -> removed.toDomain(
            InMemoryParkingSpotRepository.findBy(removed.parkingSpotId))
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

  @AllArgsConstructor
  private static class OccupationEntity {
    final OccupationId occupationId;
    final Occupant occupant;
    final ParkingSpotId parkingSpotId;
    final List<ParkingSpotSectionId> sections;

    Occupation toDomain(List<ParkingSpotSection> sections) {
      return new Occupation(
          occupationId,
          occupant,
          parkingSpotId,
          sections
      );
    }
  }

}

@RequiredArgsConstructor
class InMemoryParkingSpotRepository implements ParkingSpotRepository {

  private static final Map<ParkingSpotSectionId, ParkingSpotSectionEntity> DATABASE = new ConcurrentHashMap<>();

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
            .map(entity -> entity.toDomain(
                InMemoryOccupationRepository.findFor(entity.sectionId).get())
            )
            .toList());
  }

  @Override
  public ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.parkingSpotId.equals(parkingSpotId))
            .map(entity -> entity.toDomain(
                InMemoryOccupationRepository.findFor(entity.sectionId).get())
            )
            .toList());
  }

  static List<ParkingSpotSection> findBy(ParkingSpotId parkingSpotId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .map(entity -> entity.toDomain(
            InMemoryOccupationRepository.findFor(entity.sectionId).get()
        ))
        .toList();
  }

  @AllArgsConstructor
  private static class ParkingSpotSectionEntity {
    final ParkingSpotId parkingSpotId;
    final ParkingSpotSectionId sectionId;
    int version;

    ParkingSpotSection toDomain(OccupationId occupationId) {
      return new ParkingSpotSection(
          parkingSpotId,
          sectionId,
          occupationId,
          version
      );
    }

  }

}
