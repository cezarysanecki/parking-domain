package pl.cezarysanecki.parkingdomain.cleaning.policies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;

@Slf4j
@Component
public class CallingCleaningWhenSpotsDirtyPolicy {

  private final CleaningFacade cleaningFacade;
  private final int numberOfDirtyParkingSpotsToCallExternalService;

  CallingCleaningWhenSpotsDirtyPolicy(
      CleaningFacade cleaningFacade,
      @Value("${business.cleaning.numberOfDirtyParkingSpotsToCallExternalService}") int numberOfDirtyParkingSpotsToCallExternalService
  ) {
    this.cleaningFacade = cleaningFacade;
    this.numberOfDirtyParkingSpotsToCallExternalService = numberOfDirtyParkingSpotsToCallExternalService;
  }

  public Result callCleaning() {
    List<ParkingSpotId> parkingSpotIds = cleaningFacade.getDirtyParkingSpots();

    if (parkingSpotIds.size() >= numberOfDirtyParkingSpotsToCallExternalService) {
      log.debug("at least {} parking spots need to be cleaned, calling external service", parkingSpotIds.size());
      cleaningFacade.callCleaning();
      return Result.Success;
    } else {
      log.debug("still not enough parking spots are dirty to call external service");
      return Result.Rejection;
    }
  }

}
