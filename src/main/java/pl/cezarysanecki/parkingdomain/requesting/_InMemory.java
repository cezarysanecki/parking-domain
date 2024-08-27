package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.persistence.EntityNotFoundException;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDate;
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
import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.TEMPLATES_DATABASE;

class InMemoryRequestableParkingSpotRepository implements RequestableParkingSpotRepository {

  private static final Map<ParkingSpotId, Integer> CURRENT_TEMPLATES_DATABASE = TEMPLATES_DATABASE;
  private static final Map<FreeTimeSlotKey, RequestableParkingSpotEntity> DATABASE = REQUESTABLE_PARKING_SPOT_DATABASE;

  @Override
  public void saveTemplate(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
    CURRENT_TEMPLATES_DATABASE.put(parkingSpotId, capacity.value());
  }

  @Override
  public List<RequestableParkingSpotTemplate> findAllTemplates() {
    return CURRENT_TEMPLATES_DATABASE.entrySet()
        .stream()
        .map(entity -> new RequestableParkingSpotTemplate(
            entity.getKey(),
            entity.getValue()
        ))
        .toList();
  }

  @Override
  public void saveAllNewFor(List<RequestableParkingSpot> requestableParkingSpots) {
    requestableParkingSpots.forEach(
        requestableParkingSpot -> {
          FreeTimeSlotKey key = new FreeTimeSlotKey(
              requestableParkingSpot.parkingSpotId(),
              requestableParkingSpot.timeSlot()
          );
          DATABASE.put(key, new RequestableParkingSpotEntity(
              key,
              requestableParkingSpot.capacity(),
              requestableParkingSpot.version().getVersion()));
        }
    );
  }

  @Override
  public RequestableParkingSpot findFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    RequestableParkingSpotEntity entity = findBy(parkingSpotId, timeSlot);
    return toDomain(entity);
  }

  @Override
  public boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    List<RequestableParkingSpotEntity> entities = DATABASE.values()
        .stream()
        .filter(entity -> entity.freeTimeSlotKey.parkingSpotId().equals(parkingSpotId))
        .toList();
    return entities.stream()
        .anyMatch(entity -> timeSlot.intersects(entity.freeTimeSlotKey.timeSlot()));
  }

  @Override
  public void deleteAllFor(LocalDate day) {
    DATABASE.values()
        .removeIf(entity -> day.equals(entity.freeTimeSlotKey.timeSlot().from().atZone(ZoneId.systemDefault()).toLocalDate()));
  }

  static RequestableParkingSpotEntity findBy(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    RequestableParkingSpotEntity entity = DATABASE.get(new FreeTimeSlotKey(parkingSpotId, timeSlot));
    if (entity == null) {
      throw new EntityNotFoundException("cannot find requestable parking spot with id " + parkingSpotId + " for time slot " + timeSlot);
    }
    return entity;
  }

  static RequestableParkingSpot toDomain(RequestableParkingSpotEntity entity) {
    return new RequestableParkingSpot(
        entity.freeTimeSlotKey.parkingSpotId(),
        entity.capacity,
        InMemoryRequestRepository.findFor(entity.freeTimeSlotKey.parkingSpotId())
            .stream()
            .map(request -> request.units)
            .reduce(0, Integer::sum),
        entity.freeTimeSlotKey.timeSlot(),
        new Version(entity.version));
  }

}

class InMemoryRequesterRepository implements RequesterRepository {

  private static final Map<RequesterId, RequesterEntity> DATABASE = REQUESTER_DATABASE;

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
    RequesterEntity entity = findRequesterBy(requesterId);
    return toDomain(entity);
  }

  static RequesterEntity findRequesterBy(RequesterId requesterId) {
    RequesterEntity entity = DATABASE.get(requesterId);
    if (entity == null) {
      throw new EntityNotFoundException("No requester found with id " + requesterId);
    }
    return entity;
  }

  static Requester toDomain(RequesterEntity entity) {
    return new Requester(
        entity.requesterId,
        InMemoryRequestRepository.findFor(entity.requesterId)
            .stream()
            .map(request -> request.requestId)
            .toList(),
        entity.limit,
        new Version(entity.version)
    );
  }

}

class InMemoryRequestRepository implements RequestRepository {

  private static final Map<RequestId, RequestEntity> DATABASE = REQUEST_DATABASE;

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
  public List<RequestForReservation> findAllBy(LocalDate day) {
    return DATABASE.values()
        .stream()
        .filter(entity -> day.equals(entity.timeSlot.from().atZone(ZoneId.systemDefault()).toLocalDate()))
        .map(entity -> new RequestForReservation(
            entity.requestId,
            entity.requesterId,
            entity.parkingSpotId,
            entity.timeSlot,
            new SpotUnits(entity.units)
        ))
        .toList();
  }

  static List<RequestEntity> findFor(ParkingSpotId parkingSpotId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.parkingSpotId.equals(parkingSpotId))
        .toList();
  }

  static List<RequestEntity> findFor(RequesterId requesterId) {
    return DATABASE.values()
        .stream()
        .filter(entity -> entity.requesterId.equals(requesterId))
        .toList();
  }

  @Override
  public void deleteAll(List<RequestId> requestIds) {
    DATABASE.values().removeIf(entity -> requestIds.contains(entity.requestId));
  }

  static Request toDomain(RequestEntity entity) {
    return new Request(
        entity.requestId,
        InMemoryRequesterRepository.toDomain(
            InMemoryRequesterRepository.findRequesterBy(entity.requesterId)),
        entity.parkingSpotId,
        entity.timeSlot,
        new SpotUnits(entity.units),
        InMemoryRequestableParkingSpotRepository.toDomain(
            InMemoryRequestableParkingSpotRepository.findBy(entity.parkingSpotId, entity.timeSlot))
    );
  }

}
