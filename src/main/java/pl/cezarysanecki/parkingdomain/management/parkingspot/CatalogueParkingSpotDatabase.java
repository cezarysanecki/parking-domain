package pl.cezarysanecki.parkingdomain.management.parkingspot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

interface CatalogueParkingSpotDatabase {

  void saveNew(ParkingSpot parkingSpot);

  class InMemoryCatalogueParkingSpotDatabase implements CatalogueParkingSpotDatabase {

    private static final Map<ParkingSpotId, ParkingSpotDatabaseRow> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void saveNew(ParkingSpot parkingSpot) {
      DATABASE.put(
          parkingSpot.getParkingSpotId(),
          new ParkingSpotDatabaseRow(
              parkingSpot.getParkingSpotId().getValue(),
              parkingSpot.getCapacity().getValue(),
              parkingSpot.getCategory()));
    }

  }

}

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ParkingSpotDatabaseRow {

  UUID parkingSpotId;
  int capacity;
  ParkingSpotCategory category;

  ParkingSpot toParkingSpot() {
    return new ParkingSpot(parkingSpotId, capacity, category);
  }

}
