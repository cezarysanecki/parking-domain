package pl.cezarysanecki.parkingdomain.parking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.commons.aggregates.AggregateRootIsStale;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.OccupantRecord;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.OccupationRecord;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.records.ParkingRecord;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReleasedOccupation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.List;
import java.util.Optional;

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Occupant.OCCUPANT;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Occupation.OCCUPATION;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Parking.PARKING;
import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.ReservedOccupation.RESERVED_OCCUPATION;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdParkingRepository implements ParkingRepository {

  private final DSLContext create;

  @Override
  public void saveNew(ParkingSpot parkingSpot) {
    create.insertInto(PARKING)
        .set(PARKING.PARKING_SPOT, parkingSpot.parkingSpotId().value())
        .set(PARKING.CAPACITY, parkingSpot.capacity().value())
        .set(PARKING.VERSION, parkingSpot.version().getVersion())
        .execute();
  }

  @Override
  public ParkingSpot loadBy(ParkingSpotId parkingSpotId) {
    ParkingRecord parkingRecord = create
        .selectFrom(PARKING)
        .where(PARKING.PARKING_SPOT.eq(parkingSpotId.value()))
        .forUpdate()
        .fetchOne();
    if (parkingRecord == null) {
      throw new EntityNotFoundException("cannot find parking spot with id " + parkingSpotId);
    }

    Integer occupiedSpace = create
        .selectFrom(OCCUPATION)
        .where(OCCUPATION.PARKING_SPOT.eq(parkingSpotId.value()))
        .fetch()
        .map(OccupationRecord::getSpotUnits)
        .stream()
        .reduce(0, Integer::sum);
    List<pl.cezarysanecki.parkingdomain.parking.Reservation> reservations = create
        .selectFrom(RESERVED_OCCUPATION)
        .where(RESERVED_OCCUPATION.PARKING_SPOT.eq(parkingSpotId.value()))
        .fetch()
        .map(record -> new pl.cezarysanecki.parkingdomain.parking.Reservation(
            new ReservationId(record.getReservation()),
            new SpotUnits(record.getSpotUnits())
        ))
        .stream().toList();

    return new ParkingSpot(
        parkingSpotId,
        occupiedSpace,
        reservations,
        new ParkingSpotCapacity(parkingRecord.getCapacity()),
        new Version(parkingRecord.getVersion()));
  }

  void saveCheckingVersion(ParkingSpot parkingSpot) {
    int result = create
        .update(PARKING)
        .set(PARKING.VERSION, parkingSpot.version().getVersion() + 1)
        .where(PARKING.PARKING_SPOT.eq(parkingSpot.parkingSpotId().value()))
        .and(PARKING.VERSION.eq(parkingSpot.version().getVersion()))
        .execute();
    if (result == 0) {
      throw new AggregateRootIsStale("Someone has updated parking spot in the meantime, parking spot: " + parkingSpot.parkingSpotId());
    }
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdReservedOccupationRepository implements ReservedOccupationRepository {

  private final DSLContext create;

  @Override
  public void storeFor(ParkingSpotId parkingSpotId, ReservationId reservationId, SpotUnits spotUnits) {
    create.insertInto(RESERVED_OCCUPATION)
        .set(RESERVED_OCCUPATION.PARKING_SPOT, parkingSpotId.value())
        .set(RESERVED_OCCUPATION.RESERVATION, reservationId.value())
        .set(RESERVED_OCCUPATION.SPOT_UNITS, spotUnits.value())
        .execute();
  }

  @Override
  public void remove(List<ReservationId> reservations) {
    if (reservations.isEmpty()) {
      return;
    }
    create.delete(RESERVED_OCCUPATION)
        .where(RESERVED_OCCUPATION.RESERVATION.in(reservations.stream().map(ReservationId::value).toList()))
        .execute();
  }
}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdOccupantRepository implements OccupantRepository {

  private final DSLContext create;

  @Override
  public Occupant findBy(OccupantId occupantId) {
    OccupantRecord occupantRecord = create
        .selectFrom(OCCUPANT)
        .where(OCCUPANT.CLIENT.eq(occupantId.value()))
        .forUpdate()
        .fetchOne();
    if (occupantRecord == null) {
      throw new EntityNotFoundException("cannot find occupant with id " + occupantId);
    }

    List<OccupationId> occupations = create
        .selectFrom(OCCUPATION)
        .where(OCCUPATION.OCCUPANT.eq(occupantId.value()))
        .fetch()
        .map(record -> new OccupationId(record.getId()))
        .stream().toList();

    return new Occupant(
        new OccupantId(occupantRecord.getClient()),
        occupations,
        new Version(occupantRecord.getVersion()));
  }

  @Override
  public void saveNew(Occupant occupant) {
    create.insertInto(OCCUPANT)
        .set(OCCUPANT.CLIENT, occupant.occupantId().value())
        .set(OCCUPANT.VERSION, occupant.version().getVersion())
        .execute();
  }

  void saveCheckingVersion(Occupant occupant) {
    int result = create
        .update(OCCUPANT)
        .set(OCCUPANT.VERSION, occupant.version().getVersion() + 1)
        .where(OCCUPANT.CLIENT.eq(occupant.occupantId().value()))
        .and(OCCUPANT.VERSION.eq(occupant.version().getVersion()))
        .execute();
    if (result == 0) {
      throw new AggregateRootIsStale("Someone has updated occupant in the meantime, occupant: " + occupant.occupantId());
    }
  }

}

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdOccupationRepository implements OccupationRepository {

  private final DSLContext create;
  private final ProdParkingRepository parkingRepository;
  private final ProdOccupantRepository occupantRepository;

  @Override
  public void saveCheckingVersion(Occupation occupation) {
    ParkingSpot parkingSpot = occupation.parkingSpot();
    Occupant occupant = occupation.occupant();

    create.insertInto(OCCUPATION)
        .set(OCCUPATION.ID, occupation.occupationId().value())
        .set(OCCUPATION.OCCUPANT, occupation.occupant().occupantId().value())
        .set(OCCUPATION.PARKING_SPOT, occupation.parkingSpot().parkingSpotId().value())
        .set(OCCUPATION.SPOT_UNITS, occupation.occupiedUnits().value())
        .execute();
    parkingRepository.saveCheckingVersion(parkingSpot);
    occupantRepository.saveCheckingVersion(occupant);
  }

  @Override
  public Optional<ReleasedOccupation> delete(OccupationId occupationId) {
    return create
        .deleteFrom(OCCUPATION)
        .where(OCCUPATION.ID.eq(occupationId.value()))
        .returning()
        .fetchOptional()
        .map(record -> new ReleasedOccupation(
            new OccupationId(record.getId()),
            new OccupantId(record.getOccupant()),
            new ParkingSpotId(record.getParkingSpot()),
            new SpotUnits(record.getSpotUnits())
        ));
  }

}
