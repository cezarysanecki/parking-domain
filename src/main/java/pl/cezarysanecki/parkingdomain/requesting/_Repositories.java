package pl.cezarysanecki.parkingdomain.requesting;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.commons.EntityNotFound;
import pl.cezarysanecki.parkingdomain.commons.aggregates.AggregateRootIsStale;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.RequestRecord;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.RequestableParkingSpotRecord;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.RequesterRecord;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.jooq.impl.DSL.row;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Request.REQUEST;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.RequestableParkingSpot.REQUESTABLE_PARKING_SPOT;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.RequestableParkingSpotTemplate.REQUESTABLE_PARKING_SPOT_TEMPLATE;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Requester.REQUESTER;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdRequestableParkingSpotRepository implements RequestableParkingSpotRepository {

  private final DSLContext create;

  @Override
  public void saveTemplate(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
    create
        .insertInto(REQUESTABLE_PARKING_SPOT_TEMPLATE)
        .set(REQUESTABLE_PARKING_SPOT_TEMPLATE.PARKING_SPOT, parkingSpotId.value())
        .set(REQUESTABLE_PARKING_SPOT_TEMPLATE.CAPACITY, capacity.value())
        .execute();
  }

  @Override
  public List<RequestableParkingSpotTemplate> findAllTemplates() {
    return create
        .selectFrom(REQUESTABLE_PARKING_SPOT_TEMPLATE)
        .stream()
        .map(record -> new RequestableParkingSpotTemplate(
            new ParkingSpotId(record.getParkingSpot()),
            record.getCapacity()
        ))
        .toList();
  }

  @Override
  public void saveAllNewFor(List<RequestableParkingSpot> requestableParkingSpots) {
    var valuesToInsert = requestableParkingSpots.stream()
        .map(r -> row(
            r.parkingSpotId().value(),
            LocalDateTime.ofInstant(r.timeSlot().from(), ZoneId.systemDefault()),
            LocalDateTime.ofInstant(r.timeSlot().to(), ZoneId.systemDefault()),
            r.capacity(),
            r.version().getVersion()
        ))
        .toList();

    create
        .insertInto(REQUESTABLE_PARKING_SPOT,
            REQUESTABLE_PARKING_SPOT.PARKING_SPOT,
            REQUESTABLE_PARKING_SPOT.FROM,
            REQUESTABLE_PARKING_SPOT.TO,
            REQUESTABLE_PARKING_SPOT.CAPACITY,
            REQUESTABLE_PARKING_SPOT.VERSION)
        .valuesOfRows(valuesToInsert)
        .execute();
  }

  @Override
  public RequestableParkingSpot findFor(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    RequestableParkingSpotRecord requestableParkingSpotRecord = create
        .selectFrom(REQUESTABLE_PARKING_SPOT)
        .where(REQUESTABLE_PARKING_SPOT.PARKING_SPOT.eq(parkingSpotId.value()))
        .and(REQUESTABLE_PARKING_SPOT.FROM.eq(LocalDateTime.ofInstant(timeSlot.from(), ZoneId.systemDefault())))
        .and(REQUESTABLE_PARKING_SPOT.TO.eq(LocalDateTime.ofInstant(timeSlot.to(), ZoneId.systemDefault())))
        .forUpdate()
        .fetchOne();
    if (requestableParkingSpotRecord == null) {
      throw new EntityNotFound("cannot find requestable parking spot for id " + parkingSpotId.value() + " and " + timeSlot);
    }

    Integer occupiedSpace = create
        .selectFrom(REQUEST)
        .where(REQUEST.PARKING_SPOT.eq(parkingSpotId.value()))
        .and(REQUEST.FROM.eq(LocalDateTime.ofInstant(timeSlot.from(), ZoneId.systemDefault())))
        .and(REQUEST.TO.eq(LocalDateTime.ofInstant(timeSlot.to(), ZoneId.systemDefault())))
        .stream()
        .map(RequestRecord::getUnits)
        .reduce(0, Integer::sum);

    return new RequestableParkingSpot(
        parkingSpotId,
        requestableParkingSpotRecord.getCapacity(),
        occupiedSpace,
        timeSlot,
        new Version(requestableParkingSpotRecord.getVersion()));
  }

  @Override
  public boolean intersects(ParkingSpotId parkingSpotId, TimeSlot timeSlot) {
    return create
        .selectFrom(REQUESTABLE_PARKING_SPOT)
        .where(REQUESTABLE_PARKING_SPOT.PARKING_SPOT.eq(parkingSpotId.value()))
        .stream()
        .map(record -> new TimeSlot(
            record.getFrom().atZone(ZoneId.systemDefault()).toInstant(),
            record.getTo().atZone(ZoneId.systemDefault()).toInstant()
        ))
        .anyMatch(timeSlot::intersects);
  }

  @Override
  public void deleteAllFor(LocalDate day) {
    create
        .deleteFrom(REQUESTABLE_PARKING_SPOT)
        .where(REQUESTABLE_PARKING_SPOT.FROM.cast(LocalDate.class).eq(day))
        .execute();
  }

  void saveCheckingVersion(RequestableParkingSpot requestableParkingSpot) {
    int result = create
        .update(REQUESTABLE_PARKING_SPOT)
        .set(REQUESTABLE_PARKING_SPOT.VERSION, requestableParkingSpot.version().getVersion() + 1)
        .where(REQUESTABLE_PARKING_SPOT.PARKING_SPOT.eq(requestableParkingSpot.parkingSpotId().value()))
        .and(REQUESTABLE_PARKING_SPOT.VERSION.eq(requestableParkingSpot.version().getVersion()))
        .execute();
    if (result == 0) {
      throw new AggregateRootIsStale("Someone has updated requestable parking spot in the meantime, requestable parking spot: " + requestableParkingSpot.parkingSpotId());
    }
  }
}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdRequesterRepository implements RequesterRepository {

  private final DSLContext create;

  @Override
  public void saveNew(RequesterId requesterId, int limit) {
    create
        .insertInto(REQUESTER)
        .set(REQUESTER.CLIENT, requesterId.value())
        .set(REQUESTER.LIMIT, limit)
        .set(REQUESTER.VERSION, 0)
        .execute();
  }

  @Override
  public Requester findBy(RequesterId requesterId) {
    RequesterRecord requesterRecord = create
        .selectFrom(REQUESTER)
        .where(REQUESTER.CLIENT.eq(requesterId.value()))
        .forUpdate()
        .fetchOne();
    if (requesterRecord == null) {
      throw new EntityNotFound("cannot find requester with id " + requesterId);
    }

    List<RequestId> requests = create
        .selectFrom(REQUEST)
        .where(REQUEST.REQUESTER.eq(requesterId.value()))
        .stream()
        .map(record -> new RequestId(record.getId()))
        .toList();

    return new Requester(
        requesterId,
        requests,
        requesterRecord.getLimit(),
        new Version(requesterRecord.getVersion()));
  }

  void saveCheckingVersion(Requester requester) {
    int result = create
        .update(REQUESTER)
        .set(REQUESTER.VERSION, requester.version().getVersion() + 1)
        .where(REQUESTER.CLIENT.eq(requester.requesterId().value()))
        .and(REQUESTER.VERSION.eq(requester.version().getVersion()))
        .execute();
    if (result == 0) {
      throw new AggregateRootIsStale("Someone has updated requester in the meantime, requester: " + requester.requesterId());
    }
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdRequestRepository implements RequestRepository {

  private final DSLContext create;
  private final ProdRequestableParkingSpotRepository requestableParkingSpotRepository;
  private final ProdRequesterRepository requesterRepository;

  @Override
  public void saveCheckingVersion(Request request) {
    create
        .insertInto(REQUEST)
        .set(REQUEST.ID, request.requestId().value())
        .set(REQUEST.REQUESTER, request.requester().requesterId().value())
        .set(REQUEST.PARKING_SPOT, request.parkingSpotId().value())
        .set(REQUEST.FROM, LocalDateTime.ofInstant(request.timeSlot().from(), ZoneId.systemDefault()))
        .set(REQUEST.TO, LocalDateTime.ofInstant(request.timeSlot().to(), ZoneId.systemDefault()))
        .set(REQUEST.UNITS, request.spotUnits().value())
        .execute();

    requestableParkingSpotRepository.saveCheckingVersion(request.requestableParkingSpot());
    requesterRepository.saveCheckingVersion(request.requester());
  }

  @Override
  public boolean delete(RequestId requestId) {
    int result = create
        .deleteFrom(REQUEST)
        .where(REQUEST.ID.eq(requestId.value()))
        .execute();
    return result != 0;
  }

  @Override
  public List<RequestForReservation> findAllBy(LocalDate day) {
    return create
        .selectFrom(REQUEST)
        .where(REQUEST.FROM.cast(LocalDate.class).eq(day))
        .stream()
        .map(record -> new RequestForReservation(
            new RequestId(record.getId()),
            new RequesterId(record.getRequester()),
            new ParkingSpotId(record.getParkingSpot()),
            new TimeSlot(
                record.getFrom().atZone(ZoneId.systemDefault()).toInstant(),
                record.getTo().atZone(ZoneId.systemDefault()).toInstant()),
            new SpotUnits(record.getUnits())
        ))
        .toList();
  }

  @Override
  public void deleteAll(List<RequestId> requestIds) {
    if (requestIds.isEmpty()) {
      return;
    }
    create
        .deleteFrom(REQUEST)
        .where(REQUEST.ID.in(requestIds.stream().map(RequestId::value).toList()))
        .execute();
  }
}
