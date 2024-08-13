package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CleaningFacade {

  private final CleaningRepository cleaningRepository;
  private final ExternalCleaningService externalCleaningService;
  private final int numberOfDrivesAwayToConsiderParkingSpotDirty;

  public Result callCleaning() {
    externalCleaningService.call();
    return Result.Success;
  }

  public Result markCleaningAsDone() {
    cleaningRepository.resetAll();
    return Result.Success;
  }

  public List<ParkingSpotId> getDirtyParkingSpots() {
    return cleaningRepository.getAllRecordsWithCounterAbove(numberOfDrivesAwayToConsiderParkingSpotDirty);
  }

}
