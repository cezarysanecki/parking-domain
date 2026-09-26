package pl.cezarysanecki.parkingdomain.cleaning.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;

@Slf4j
@Component
public class CallingCleaningWhenSpotsDirtyUseCase {

  private final CleaningFacade cleaningFacade;
  private final int numberOfDirtyParkingSpotsToCallExternalService;

  CallingCleaningWhenSpotsDirtyUseCase(
      CleaningFacade cleaningFacade,
      @Value("${business.cleaning.number-of-dirty-parking-spots-to-call-external-service}") int numberOfDirtyParkingSpotsToCallExternalService
  ) {
    this.cleaningFacade = cleaningFacade;
    this.numberOfDirtyParkingSpotsToCallExternalService = numberOfDirtyParkingSpotsToCallExternalService;
  }

  public Result run() {
    List<ParkingSpotId> parkingSpotIds = cleaningFacade.getDirtyParkingSpots();

    if (parkingSpotIds.size() >= numberOfDirtyParkingSpotsToCallExternalService) {
      log.debug("at least {} parking spots need to be cleaned, calling external service", parkingSpotIds.size());
      return cleaningFacade.callCleaning();
    } else {
      log.debug("still not enough parking spots are dirty to call external service");
      return Result.Rejection;
    }
  }

}
