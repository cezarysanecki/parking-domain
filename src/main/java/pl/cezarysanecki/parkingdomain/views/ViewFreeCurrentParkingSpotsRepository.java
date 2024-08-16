package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;

import java.util.List;
import java.util.UUID;

public interface ViewFreeCurrentParkingSpotsRepository {

  List<ParkingSpotEntry> queryParkingSpots();

  record ParkingSpotEntry(
      UUID parkingSpotId,
      ParkingSpotCategory category,
      int spaceLeft
  ) {
  }

}
