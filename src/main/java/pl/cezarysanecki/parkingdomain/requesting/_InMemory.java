package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.persistence.EntityNotFoundException;
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.FreeTimeSlotKey;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequestableParkingSpotEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryEntities.RequesterEntity;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.REQUESTABLE_PARKING_SPOT_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.REQUESTER_DATABASE;
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.REQUEST_DATABASE;

class InMemoryRequestableParkingSpotRepository implements RequestableParkingSpotRepository {

  static final Map<ParkingSpotId, Integer> TEMPLATES_DATABASE = InMemoryRepositories.TEMPLATES_DATABASE;
  static final Map<FreeTimeSlotKey, RequestableParkingSpotEntity> DATABASE = REQUESTABLE_PARKING_SPOT_DATABASE;

  @Override
  public void saveTemplate(ParkingSpotId parkingSpotId, int numberOfSections) {
    TEMPLATES_DATABASE.put(parkingSpotId, numberOfSections);
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
  public void saveNew(RequestableParkingSpot requestableParkingSpot) {
    FreeTimeSlotKey key = new FreeTimeSlotKey(
        requestableParkingSpot.parkingSpotId(),
        requestableParkingSpot.timeSlot()
    );
    DATABASE.put(key, new RequestableParkingSpotEntity(
        key,
        requestableParkingSpot.capacity(),
        requestableParkingSpot.version().getVersion()));
  }

  @Override
  public RequestableParkingSpot findFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    return findBy(parkingSpotId, timeSlot);
  }

  @Override
  public boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    List<RequestableParkingSpotEntity> entities = DATABASE.values()
        .stream()
        .filter(entity -> entity.freeTimeSlotKey.parkingSpotId().equals(parkingSpotId))
        .toList();
    return entities.stream()
        .anyMatch(entity -> timeSlot.within(entity.freeTimeSlotKey.timeSlot()));
  }

  static RequestableParkingSpot findBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    RequestableParkingSpotEntity entity = DATABASE.get(new FreeTimeSlotKey(parkingSpotId, timeSlot));
    if (entity == null) {
      throw new EntityNotFoundException("cannot find requestable parking spot with id " + parkingSpotId + " for time slot " + timeSlot);
    }
    return new RequestableParkingSpot(
        parkingSpotId,
        entity.capacity,
        InMemoryRequestRepository.findFor(parkingSpotId).size(),
        entity.freeTimeSlotKey.timeSlot(),
        new Version(entity.version));
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
    return findRequesterBy(requesterId);
  }

  static Requester findRequesterBy(RequesterId requesterId) {
    RequesterEntity entity = DATABASE.get(requesterId);
    if (entity == null) {
      throw new EntityNotFoundException("No requester found with id " + requesterId);
    }
    return new Requester(
        entity.requesterId,
        InMemoryRequestRepository.findFor(requesterId),
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
        request.spotUnits().value()
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
            InMemoryRequestableParkingSpotRepository.findBy(entity.parkingSpotId, entity.timeSlot),
            InMemoryRequesterRepository.findRequesterBy(entity.requesterId)
        ))
        .toList();
  }

  static List<RequestId> findFor(ParkingSpotId parkingSpotId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .map(entity -> entity.requestId)
        .toList();
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

  private static Request toDomain(
      RequestEntity entity,
      RequestableParkingSpot requestableParkingSpot,
      Requester requester) {
    return new Request(
        entity.requestId,
        requester,
        entity.parkingSpotId,
        entity.timeSlot,
        new SpotUnits(entity.units),
        requestableParkingSpot
    );
  }

}
