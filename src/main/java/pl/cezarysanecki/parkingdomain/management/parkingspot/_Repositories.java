package pl.cezarysanecki.parkingdomain.management.parkingspot;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.cezarysanecki.parkingdomain.jooq.default_schema.tables.ParkingSpotCatalogue;

@Profile("!local")
@Repository
@RequiredArgsConstructor
class ProdParkingSpotRepository implements ParkingSpotRepository {

  private final DSLContext create;

  @Override
  public void saveNew(ParkingSpot parkingSpot) {
    create.insertInto(ParkingSpotCatalogue.PARKING_SPOT_CATALOGUE)
        .set(ParkingSpotCatalogue.PARKING_SPOT_CATALOGUE.ID, parkingSpot.parkingSpotId().value())
        .set(ParkingSpotCatalogue.PARKING_SPOT_CATALOGUE.CAPACITY, parkingSpot.capacity().value())
        .set(ParkingSpotCatalogue.PARKING_SPOT_CATALOGUE.CATEGORY, parkingSpot.category().name())
        .execute();
  }

}
