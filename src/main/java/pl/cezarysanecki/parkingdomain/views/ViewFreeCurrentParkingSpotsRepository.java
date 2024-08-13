package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

public interface ViewFreeCurrentParkingSpotsRepository {

  List<ParkingSpotEntry> queryParkingSpots();

  record ParkingSpotEntry(
      ParkingSpotId parkingSpotId,
      ParkingSpotCategory category,
      int spaceLeft
  ) {
  }

}
