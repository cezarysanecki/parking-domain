package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
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
import java.util.concurrent.ConcurrentHashMap;

class InMemoryRequestableSectionRepository implements RequestableSectionRepository {

  static final Map<ParkingSpotSectionId, RequestableSectionEntity> DATABASE = new ConcurrentHashMap<>();
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
        .map(entity -> entity.toDomain(
            InMemoryRequestRepository.findFor(entity.sectionId).get()
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
        .map(entity -> entity.toDomain(
            InMemoryRequestRepository.findFor(entity.sectionId).get()
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
        .map(entity -> entity.toDomain(
            InMemoryRequestRepository.findFor(entity.sectionId).get()
        ))
        .toList();
  }

  @AllArgsConstructor
  private static class RequestableSectionEntity {
    final ParkingSpotId parkingSpotId;
    final ParkingSpotSectionId sectionId;
    final TimeSlot timeSlot;
    int version;

    RequestableSection toDomain(RequestId requestId) {
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

  static final Map<RequesterId, RequesterEntity> DATABASE = new ConcurrentHashMap<>();

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
    return entity.toDomain(
        InMemoryRequestRepository.findFor(entity.requesterId)
    );
  }

  static Optional<Requester> tryFindBy(RequesterId requesterId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.requesterId.equals(requesterId))
        .map(entity -> entity.toDomain(
            InMemoryRequestRepository.findFor(requesterId)
        ))
        .findFirst();
  }

  @AllArgsConstructor
  private static class RequesterEntity {
    final RequesterId requesterId;
    final int limit;
    int version;

    Requester toDomain(List<RequestId> requests) {
      return new Requester(
          requesterId,
          requests,
          limit,
          new Version(version)
      );
    }
  }

}

class InMemoryRequestRepository implements RequestRepository {

  static final Map<RequestId, RequestEntity> DATABASE = new ConcurrentHashMap<>();

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
        .map(entity -> entity.toDomain(
            InMemoryRequesterRepository.tryFindBy(entity.requesterId).get(),
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

  @AllArgsConstructor
  private static class RequestEntity {
    final RequestId requestId;
    final RequesterId requesterId;
    final ParkingSpotId parkingSpotId;
    final TimeSlot timeSlot;
    final List<ParkingSpotSectionId> sectionsIds;

    Request toDomain(Requester requester, List<RequestableSection> sections) {
      return new Request(
          requestId,
          requester,
          parkingSpotId,
          timeSlot,
          sections
      );
    }
  }

}
