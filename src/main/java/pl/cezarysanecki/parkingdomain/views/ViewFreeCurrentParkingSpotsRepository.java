package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ViewFreeCurrentParkingSpotsRepository {

  List<ParkingSpotEntry> queryParkingSpots(Instant activationDateOfReservations);

  record ParkingSpotEntry(
      UUID parkingSpotId,
      ParkingSpotCategory category,
      int spaceLeft
  ) {
  }

}
