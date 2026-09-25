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
    log.debug("calling external service to clean parking spots");
    externalCleaningService.call();
    return Result.Success;
  }

  public Result markCleaningAsDone() {
    log.debug("cleaning is done - resetting all counters");
    cleaningRepository.resetAll();
    return Result.Success;
  }

  public List<ParkingSpotId> getDirtyParkingSpots() {
    List<ParkingSpotId> dirtyParkingSpots = cleaningRepository.getAllRecordsWithCounterAtLeast(numberOfDrivesAwayToConsiderParkingSpotDirty);
    log.debug("found {} dirty parking spots", dirtyParkingSpots.size());
    return dirtyParkingSpots;
  }

}
