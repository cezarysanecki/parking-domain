package pl.cezarysanecki.parkingdomain.management.parkingspot;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryParkingSpotRepository implements ParkingSpotRepository {

  private static final Map<ParkingSpotId, ParkingSpot> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveNew(ParkingSpot parkingSpot) {
    DATABASE.put(parkingSpot.parkingSpotId(), parkingSpot);
  }

}
