package pl.cezarysanecki.parkingdomain.parking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.Occupant;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryOccupationRepository implements OccupationRepository {

  static final Map<OccupationId, Entity> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveCheckingVersion(Occupation occupation) {
    Entity entity = new Entity(
        occupation.occupationId(),
        occupation.occupant(),
        occupation.parkingSpotId(),
        occupation.sections().stream().map(ParkingSpotSection::sectionId).toList()
    );

    DATABASE.put(occupation.occupationId(), entity);
    InMemoryParkingSpotRepository.saveOccupation(occupation.parkingSpotId(), occupation.occupationId());
  }

  @Override
  public Optional<Occupation> delete(OccupationId occupationId) {
    return Optional.ofNullable(DATABASE.remove(occupationId))
        .map(removed -> {
          InMemoryParkingSpotRepository.removeOccupation(removed.occupationId);
          return removed.toDomain(InMemoryParkingSpotRepository.findByOccupation(removed.occupationId));
        });
  }

  @AllArgsConstructor
  private static class Entity {
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

  static final Map<ParkingSpotSectionId, Entity> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveNew(ParkingSpotSectionsGrouped parkingSpotSectionsGrouped) {
    parkingSpotSectionsGrouped.sections()
        .forEach(section -> DATABASE.put(
            section.sectionId(),
            new Entity(section.parkingSpotId(), section.sectionId())));
  }

  @Override
  public ParkingSpotSectionsGrouped loadFreeSectionsFor(ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.parkingSpotId.equals(parkingSpotId)
                && section.occupationId == null)
            .limit(spotUnits.value())
            .map(Entity::toDomain)
            .toList());
  }

  @Override
  public ParkingSpotSectionsGrouped loadBy(ParkingSpotId parkingSpotId) {
    return new ParkingSpotSectionsGrouped(
        DATABASE.values()
            .stream()
            .filter(section -> section.parkingSpotId.equals(parkingSpotId))
            .map(Entity::toDomain)
            .toList());
  }

  static void saveOccupation(ParkingSpotId parkingSpotId, OccupationId occupationId) {
    DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .forEach(entity -> entity.occupationId = occupationId);
  }

  static void removeOccupation(OccupationId occupationId) {
    DATABASE.values()
        .stream()
        .filter(entity -> entity.occupationId.equals(occupationId))
        .forEach(entity -> entity.occupationId = null);
  }

  static List<ParkingSpotSection> findByOccupation(OccupationId occupationId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.occupationId.equals(occupationId))
        .map(Entity::toDomain)
        .toList();
  }

  private static class Entity {
    final ParkingSpotId parkingSpotId;
    final ParkingSpotSectionId sectionId;
    OccupationId occupationId;
    int version;

    private Entity(ParkingSpotId parkingSpotId, ParkingSpotSectionId sectionId) {
      this.parkingSpotId = parkingSpotId;
      this.sectionId = sectionId;
      this.occupationId = null;
      this.version = 0;
    }

    ParkingSpotSection toDomain() {
      return new ParkingSpotSection(
          parkingSpotId,
          sectionId,
          occupationId,
          version
      );
    }

  }

}
