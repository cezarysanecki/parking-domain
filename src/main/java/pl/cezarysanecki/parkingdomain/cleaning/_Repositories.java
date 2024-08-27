package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

import static pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.Cleaning.CLEANING;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdCleaningRepository implements CleaningRepository {

  private final DSLContext create;

  @Override
  public void increaseCounterFor(ParkingSpotId parkingSpotId) {
    create.insertInto(CLEANING)
        .set(CLEANING.PARKING_SPOT, parkingSpotId.value())
        .set(CLEANING.COUNTER, 1)
        .onDuplicateKeyUpdate()
        .set(CLEANING.COUNTER, CLEANING.COUNTER.plus(1))
        .execute();
  }

  @Override
  public void resetAll() {
    create.delete(CLEANING)
        .execute();
  }

  @Override
  public List<ParkingSpotId> getAllRecordsWithCounterAbove(int limit) {
    return create.select(CLEANING.PARKING_SPOT)
        .from(CLEANING)
        .where(CLEANING.COUNTER.greaterOrEqual(limit))
        .fetch()
        .map(record -> new ParkingSpotId(record.get(CLEANING.PARKING_SPOT)))
        .stream().toList();
  }
}
