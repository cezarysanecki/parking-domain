package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.shared.ParkingOpeningHours;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CleaningFacade {

  private final CleaningRepository cleaningRepository;
  private final ExternalCleaningService externalCleaningService;
  private final DateProvider dateProvider;
  private final int numberOfDrivesAwayToConsiderParkingSpotDirty;

  public Result callCleaning() {
    Instant now = dateProvider.now();
    if (!ParkingOpeningHours.isTechnicalBreak(now)) {
      log.debug("cannot call external cleaning service at {}, parking spots can be cleaned only during technical break [{}, {})",
          now, ParkingOpeningHours.CLOSING, ParkingOpeningHours.OPENING);
      return Result.Rejection;
    }
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
