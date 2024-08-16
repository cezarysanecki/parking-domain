package pl.cezarysanecki.parkingdomain.management.parkingspot;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.Map;

import static pl.cezarysanecki.parkingdomain._local.InMemoryRepositories.PARKING_SPOT_DATABASE;

class InMemoryParkingSpotRepository implements ParkingSpotRepository {

  private static final Map<ParkingSpotId, ParkingSpot> DATABASE = PARKING_SPOT_DATABASE;

  @Override
  public void saveNew(ParkingSpot parkingSpot) {
    DATABASE.put(parkingSpot.parkingSpotId(), parkingSpot);
  }

}
