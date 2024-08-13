package pl.cezarysanecki.parkingdomain.web;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

public interface ViewFreeCurrentParkingSpotsRepository {

  List<ParkingSpotEntry> query();

  record ParkingSpotEntry(
      ParkingSpotId parkingSpotId,
      ParkingSpotCategory category,
      int spaceLeft
  ) {
  }

}
