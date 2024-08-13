package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.persistence.EntityNotFoundException;
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotSectionId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestableSectionEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequesterEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.REQUESTABLE_SECTION_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.REQUESTER_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.REQUEST_DATABASE;

class InMemoryRequestableSectionRepository implements RequestableSectionRepository {

  static final Map<ParkingSpotSectionId, RequestableSectionEntity> DATABASE = REQUESTABLE_SECTION_DATABASE;
  static final Map<ParkingSpotId, List<ParkingSpotSectionId>> TEMPLATES_DATABASE = InMemoryRepositories.TEMPLATES_DATABASE;

  @Override
  public void saveTemplate(ParkingSpotId parkingSpotId, List<ParkingSpotSectionId> sections) {
    TEMPLATES_DATABASE.put(parkingSpotId, sections);
  }

  @Override
  public List<RequestableParkingSpotTemplate> findAllTemplates() {
    return TEMPLATES_DATABASE.entrySet()
        .stream()
        .map(entity -> new RequestableParkingSpotTemplate(
            entity.getKey(),
            entity.getValue()
        ))
        .toList();
  }

  @Override
  public void saveNew(RequestableSectionsGrouped requestableSectionsGrouped) {
    requestableSectionsGrouped.sections()
        .stream()
        .map(section -> new RequestableSectionEntity(
            section.parkingSpotId(),
            section.sectionId(),
            section.timeSlot(),
            0
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
        .map(entity -> toDomain(
            entity,
            InMemoryRequestRepository.findFor(entity.sectionId).orElse(null)
        ))
        .toList();
    return new RequestableSectionsGrouped(sections);
  }

  @Override
  public RequestableSectionsGrouped loadBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    List<RequestableSection> sections = DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId)
            && entity.timeSlot.equals(timeSlot))
        .map(entity -> toDomain(
            entity,
            InMemoryRequestRepository.findFor(entity.sectionId).orElse(null)
        ))
        .toList();
    return new RequestableSectionsGrouped(sections);
  }

  @Override
  public boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    List<RequestableSectionEntity> entities = DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .toList();
    return entities.stream()
        .anyMatch(entity -> timeSlot.within(entity.timeSlot));
  }

  static List<RequestableSection> findBy(List<ParkingSpotSectionId> sections) {
    return DATABASE.values()
        .stream()
        .filter(entity -> sections.contains(entity.sectionId))
        .map(entity -> toDomain(
            entity,
            InMemoryRequestRepository.findFor(entity.sectionId).orElse(null)
        ))
        .toList();
  }

  private static RequestableSection toDomain(RequestableSectionEntity entity, RequestId requestId) {
    return new RequestableSection(
        entity.parkingSpotId,
        entity.sectionId,
        entity.timeSlot,
        requestId,
        new Version(entity.version)
    );
  }

}

class InMemoryRequesterRepository implements RequesterRepository {

  static final Map<RequesterId, RequesterEntity> DATABASE = REQUESTER_DATABASE;

  @Override
  public void saveNew(RequesterId requesterId, int limit) {
    DATABASE.put(requesterId, new RequesterEntity(
        requesterId,
        limit,
        0
    ));
  }

  @Override
  public Requester findBy(RequesterId requesterId) {
    RequesterEntity entity = DATABASE.get(requesterId);
    if (entity == null) {
      throw new EntityNotFoundException("No requester found with id " + requesterId);
    }
    return toDomain(
        entity,
        InMemoryRequestRepository.findFor(entity.requesterId)
    );
  }

  static Optional<Requester> tryFindBy(RequesterId requesterId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.requesterId.equals(requesterId))
        .map(entity -> toDomain(
            entity,
            InMemoryRequestRepository.findFor(requesterId)
        ))
        .findFirst();
  }

  private static Requester toDomain(RequesterEntity entity, List<RequestId> requests) {
    return new Requester(
        entity.requesterId,
        requests,
        entity.limit,
        new Version(entity.version)
    );
  }

}

class InMemoryRequestRepository implements RequestRepository {

  static final Map<RequestId, RequestEntity> DATABASE = REQUEST_DATABASE;

  @Override
  public void saveCheckingVersion(Request request) {
    DATABASE.put(request.requestId(), new RequestEntity(
        request.requestId(),
        request.requester().requesterId(),
        request.parkingSpotId(),
        request.timeSlot(),
        request.sections().stream().map(RequestableSection::sectionId).toList()
    ));
  }

  @Override
  public boolean delete(RequestId requestId) {
    return DATABASE.remove(requestId) != null;
  }

  @Override
  public List<Request> findAllBy(Instant date) {
    return DATABASE.values()
        .stream()
        .filter(entity -> date.atZone(ZoneId.systemDefault()).toLocalDate().equals(
            entity.timeSlot.from().atZone(ZoneId.systemDefault()).toLocalDate()))
        .map(entity -> toDomain(
            entity,
            InMemoryRequesterRepository.tryFindBy(entity.requesterId).orElse(null),
            InMemoryRequestableSectionRepository.findBy(entity.sectionsIds)
        ))
        .toList();
  }

  static Optional<RequestId> findFor(ParkingSpotSectionId sectionId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.sectionsIds.contains(sectionId))
        .map(entity -> entity.requestId)
        .findFirst();
  }

  static List<RequestId> findFor(RequesterId requesterId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.requesterId.equals(requesterId))
        .map(entity -> entity.requestId)
        .toList();
  }

  @Override
  public void deleteAll(List<RequestId> requestIds) {
    DATABASE.values().removeIf(entity -> requestIds.contains(entity.requestId));
  }

  private static Request toDomain(RequestEntity entity, Requester requester, List<RequestableSection> sections) {
    return new Request(
        entity.requestId,
        requester,
        entity.parkingSpotId,
        entity.timeSlot,
        sections
    );
  }

}
