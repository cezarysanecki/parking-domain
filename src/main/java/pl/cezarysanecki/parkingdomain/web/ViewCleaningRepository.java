package pl.cezarysanecki.parkingdomain.web;

import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

public interface ViewCleaningRepository {

  CleaningView query();

  record CleaningView(
      Long parkingSpotsExceedingThreshold,
      List<ParkingSpot> records
  ) {

    public record ParkingSpot(
        ParkingSpotId parkingSpotId,
        int counter
    ) {
    }

  }

}
