package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.persistence.EntityNotFoundException;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryRequestableSectionRepository implements RequestableSectionRepository {

  static final Map<ParkingSpotSectionId, Entity> DATABASE = new ConcurrentHashMap<>();
  static final Map<ParkingSpotId, List<ParkingSpotSectionId>> TEMPLATES_DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveTemplate(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections) {
    TEMPLATES_DATABASE.put(parkingSpotId, sections);
  }

  @Override
  public RequestableParkingSpotTemplate findTemplateBy(ParkingSpotId parkingSpotId) {
    List<ParkingSpotSectionId> sections = TEMPLATES_DATABASE.get(parkingSpotId);
    if (sections == null || sections.isEmpty()) {
      throw new EntityNotFoundException("No parking spot template found with id " + parkingSpotId);
    }
    return new RequestableParkingSpotTemplate(parkingSpotId, sections);
  }

  @Override
  public void saveNew(RequestableSectionsGrouped requestableSectionsGrouped) {
    requestableSectionsGrouped.sections()
        .stream()
        .map(section -> new Entity(
            section.parkingSpotId(),
            section.sectionId(),
            section.timeSlot()
        ))
        .forEach(entity -> DATABASE.put(entity.sectionId, entity));
  }

  @Override
  public RequestableSectionsGrouped findFreeSectionsFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot, SpotUnits spotUnits) {
    List<RequestableSection> sections = DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId)
            && entity.timeSlot.equals(timeSlot))
        .limit(spotUnits.value())
        .map(Entity::toDomain)
        .toList();
    return new RequestableSectionsGrouped(sections);
  }

  @Override
  public RequestableSectionsGrouped loadBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    List<RequestableSection> sections = DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId)
            && entity.timeSlot.equals(timeSlot))
        .map(Entity::toDomain)
        .toList();
    return new RequestableSectionsGrouped(sections);
  }

  @Override
  public boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    List<Entity> entities = DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .toList();
    return entities.stream()
        .anyMatch(entity -> timeSlot.within(entity.timeSlot));
  }

  private static class Entity {
    final ParkingSpotId parkingSpotId;
    final ParkingSpotSectionId sectionId;
    final TimeSlot timeSlot;
    RequestId requestId;
    int version;

    private Entity(ParkingSpotId parkingSpotId, ParkingSpotSectionId sectionId, TimeSlot timeSlot) {
      this.parkingSpotId = parkingSpotId;
      this.sectionId = sectionId;
      this.timeSlot = timeSlot;
      this.requestId = null;
      this.version = 0;
    }

    RequestableSection toDomain() {
      return new RequestableSection(
          parkingSpotId,
          sectionId,
          timeSlot,
          requestId,
          new Version(version)
      );
    }
  }

}

class InMemoryRequesterRepository implements RequesterRepository {

  static final Map<RequesterId, Entity> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveNew(RequesterId requesterId, int limit) {
    DATABASE.put(requesterId, new Entity(
        requesterId,
        limit
    ));
  }

  @Override
  public Requester findBy(RequesterId requesterId) {
    Entity entity = DATABASE.get(requesterId);
    if (entity == null) {
      throw new EntityNotFoundException("No requester found with id " + requesterId);
    }
    return entity.toDomain();
  }

  private static class Entity {
    final RequesterId requesterId;
    final List<RequestId> requests;
    final int limit;
    int version;

    Entity(RequesterId requesterId, int limit) {
      this.requesterId = requesterId;
      this.requests = new ArrayList<>();
      this.limit = limit;
      this.version = 0;
    }

    Requester toDomain() {
      return new Requester(
          requesterId,
          requests,
          limit,
          new Version(version)
      );
    }
  }

}

class InRequestRepository implements RequestRepository {

  static final Map<RequestId, Entity> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveCheckingVersion(Request request) {

  }

  @Override
  public boolean delete(RequestId requestId) {
    return false;
  }

  @Override
  public List<Request> findAllBy(Instant date) {
    return List.of();
  }

  @Override
  public void deleteAll(List<RequestId> requestIds) {

  }

  private static class Entity {
    final RequesterId requesterId;
    final List<RequestId> requests;
    final int limit;
    int version;

    Entity(RequesterId requesterId, int limit) {
      this.requesterId = requesterId;
      this.requests = new ArrayList<>();
      this.limit = limit;
      this.version = 0;
    }

    Requester toDomain() {
      return new Requester(
          requesterId,
          requests,
          limit,
          new Version(version)
      );
    }
  }

}
