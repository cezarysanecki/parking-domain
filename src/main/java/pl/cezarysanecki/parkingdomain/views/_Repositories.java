package pl.cezarysanecki.parkingdomain.views;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;

import java.util.List;
import java.util.UUID;

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Cleaning.CLEANING;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.ClientCatalogue.CLIENT_CATALOGUE;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Occupation.OCCUPATION;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.ParkingSpotCatalogue.PARKING_SPOT_CATALOGUE;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Request.REQUEST;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.RequestableParkingSpot.REQUESTABLE_PARKING_SPOT;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Reservation.RESERVATION;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.ReservedOccupation.RESERVED_OCCUPATION;

@Profile("!local")
@Repository
class ProdViewCleaningRepository implements ViewCleaningRepository {

  private final DSLContext create;
  private final int numberOfDrivesAwayToConsiderParkingSpotDirty;

  ProdViewCleaningRepository(
      DSLContext create,
      @Value("${business.cleaning.number-of-drives-away-to-consider-parking-spot-dirty}") int numberOfDrivesAwayToConsiderParkingSpotDirty
  ) {
    this.create = create;
    this.numberOfDrivesAwayToConsiderParkingSpotDirty = numberOfDrivesAwayToConsiderParkingSpotDirty;
  }

  @Override
  public CleaningView queryCleaning() {
    List<CleaningView.ParkingSpot> entries = create
        .selectFrom(CLEANING)
        .stream()
        .map(record -> new CleaningView.ParkingSpot(
            record.getParkingSpot(),
            record.getCounter()
        ))
        .toList();
    return new CleaningView(
        entries.stream()
            .filter(entry -> entry.counter() >= numberOfDrivesAwayToConsiderParkingSpotDirty)
            .count(),
        entries);
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdViewCurrentRequestsRepository implements ViewCurrentRequestsRepository {

  private final DSLContext create;

  @Override
  public List<RequestEntry> queryRequests() {
    return create
        .selectFrom(REQUEST)
        .stream()
        .map(record -> new RequestEntry(
            record.getId(),
            record.getRequester(),
            record.getParkingSpot(),
            record.getUnits()
        ))
        .toList();
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdViewCurrentStateOfClientRepository implements ViewCurrentStateOfClientRepository {

  private final DSLContext create;

  @Override
  public List<CurrentStateEntry> queryAll() {
    return create
        .select(CLIENT_CATALOGUE.ID)
        .from(CLIENT_CATALOGUE)
        .stream()
        .map(record -> new ClientId(record.get(CLIENT_CATALOGUE.ID)))
        .map(this::queryFor)
        .toList();
  }

  @Override
  public CurrentStateEntry queryFor(ClientId clientId) {
    List<UUID> occupations = create
        .select(OCCUPATION.ID)
        .from(OCCUPATION)
        .where(OCCUPATION.OCCUPANT.eq(clientId.value()))
        .stream()
        .map(record -> record.get(OCCUPATION.ID))
        .toList();
    List<UUID> requests = create
        .select(REQUEST.ID)
        .from(REQUEST)
        .where(REQUEST.REQUESTER.eq(clientId.value()))
        .stream()
        .map(record -> record.get(REQUEST.ID))
        .toList();
    List<UUID> reservations = create
        .select(RESERVATION.ID)
        .from(RESERVATION)
        .where(RESERVATION.OWNER.eq(clientId.value()))
        .stream()
        .map(record -> record.get(RESERVATION.ID))
        .toList();
    return new CurrentStateEntry(
        clientId.value(),
        occupations,
        requests,
        reservations);
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdViewFreeCurrentParkingSpotsRepository implements ViewFreeCurrentParkingSpotsRepository {

  private final DSLContext create;

  @Override
  public List<ParkingSpotEntry> queryParkingSpots() {
    return create
        .selectFrom(PARKING_SPOT_CATALOGUE)
        .stream()
        .map(record -> {
          Integer occupiedSpace = create
              .select(OCCUPATION.SPOT_UNITS)
              .from(OCCUPATION)
              .where(OCCUPATION.PARKING_SPOT.eq(record.getId()))
              .stream()
              .map(occupationRecord -> occupationRecord.get(OCCUPATION.SPOT_UNITS))
              .reduce(0, Integer::sum);
          Integer reservedSpace = create
              .select(RESERVED_OCCUPATION.SPOT_UNITS)
              .from(RESERVED_OCCUPATION)
              .where(RESERVED_OCCUPATION.PARKING_SPOT.eq(record.getId()))
              .stream()
              .map(reservedOccupationRecord -> reservedOccupationRecord.get(RESERVED_OCCUPATION.SPOT_UNITS))
              .reduce(0, Integer::sum);

          return new ParkingSpotEntry(
              record.getId(),
              ParkingSpotCategory.valueOf(record.getCategory()),
              record.getCapacity() - (occupiedSpace - reservedSpace));
        })
        .toList();
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdViewFreeTimeSlotsRepository implements ViewFreeTimeSlotsRepository {

  private final DSLContext create;

  @Override
  public List<FreeTimeSlotEntry> queryFreeTimeSlots() {
    return create
        .select()
        .from(REQUESTABLE_PARKING_SPOT)
        .join(PARKING_SPOT_CATALOGUE, JoinType.CROSS_JOIN)
        .on(PARKING_SPOT_CATALOGUE.ID.eq(REQUESTABLE_PARKING_SPOT.PARKING_SPOT))
        .stream()
        .map(record -> {
          Integer requestedSpace = create
              .select(REQUEST.UNITS)
              .from(REQUEST)
              .where(REQUEST.PARKING_SPOT.eq(record.get(REQUESTABLE_PARKING_SPOT.PARKING_SPOT)))
              .and(REQUEST.FROM.eq(record.get(REQUESTABLE_PARKING_SPOT.FROM)))
              .and(REQUEST.TO.eq(record.get(REQUESTABLE_PARKING_SPOT.TO)))
              .stream()
              .map(requestRecord -> requestRecord.get(REQUEST.UNITS))
              .reduce(0, Integer::sum);

          return new FreeTimeSlotEntry(
              record.get(PARKING_SPOT_CATALOGUE.ID),
              ParkingSpotCategory.valueOf(record.get(PARKING_SPOT_CATALOGUE.CATEGORY)),
              record.get(REQUESTABLE_PARKING_SPOT.FROM),
              record.get(REQUESTABLE_PARKING_SPOT.TO),
              record.get(PARKING_SPOT_CATALOGUE.CAPACITY) - requestedSpace
          );
        })
        .toList();
  }

}
