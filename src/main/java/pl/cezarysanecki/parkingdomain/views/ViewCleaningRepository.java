package pl.cezarysanecki.parkingdomain.views;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

public interface ViewCleaningRepository {

  CleaningView queryCleaning();

  record CleaningView(
      long parkingSpotsExceedingThreshold,
      List<ParkingSpot> records
  ) {

    public record ParkingSpot(
        ParkingSpotId parkingSpotId,
        int counter
    ) {
    }

  }

}
