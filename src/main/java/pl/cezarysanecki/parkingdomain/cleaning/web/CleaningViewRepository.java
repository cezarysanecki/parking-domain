package pl.cezarysanecki.parkingdomain.cleaning.web;

import java.util.List;
import java.util.UUID;

public interface CleaningViewRepository {

  CleaningView queryForCleaningView();

  record CleaningView(
      Long exceedsOfThreshold,
      List<ParkingSpot> parkingSpots
  ) {

    public record ParkingSpot(
        UUID parkingSpotId,
        Integer counter
    ) {
    }

  }

}
