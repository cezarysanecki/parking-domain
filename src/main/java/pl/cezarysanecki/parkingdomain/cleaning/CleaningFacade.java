package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CleaningFacade {

  private static final LocalTime TECHNICAL_BREAK_START = LocalTime.of(1, 0);
  private static final LocalTime TECHNICAL_BREAK_END = LocalTime.of(5, 0);

  private final CleaningRepository cleaningRepository;
  private final ExternalCleaningService externalCleaningService;
  private final DateProvider dateProvider;
  private final int numberOfDrivesAwayToConsiderParkingSpotDirty;

  public Result callCleaning() {
    LocalTime currentTime = dateProvider.now().atZone(DateProvider.ZONE_OFFSET).toLocalTime();
    if (!isDuringTechnicalBreak(currentTime)) {
      log.debug("cannot call external cleaning service at {}, parking spots can be cleaned only during technical break [{}, {})",
          currentTime, TECHNICAL_BREAK_START, TECHNICAL_BREAK_END);
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
    List<ParkingSpotId> allRecordsWithCounterAbove = cleaningRepository.getAllRecordsWithCounterAbove(numberOfDrivesAwayToConsiderParkingSpotDirty);
    log.debug("found {} dirty parking spots", allRecordsWithCounterAbove.size());
    return allRecordsWithCounterAbove;
  }

  private static boolean isDuringTechnicalBreak(LocalTime time) {
    return !time.isBefore(TECHNICAL_BREAK_START) && time.isBefore(TECHNICAL_BREAK_END);
  }

}
