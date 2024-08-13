package pl.cezarysanecki.parkingdomain.views;

import java.util.List;
import java.util.UUID;

public interface ViewCleaningRepository {

  CleaningView queryCleaning();

  record CleaningView(
      long parkingSpotsExceedingThreshold,
      List<ParkingSpot> records
  ) {

    public record ParkingSpot(
        UUID parkingSpotId,
        int counter
    ) {
    }

  }

}
