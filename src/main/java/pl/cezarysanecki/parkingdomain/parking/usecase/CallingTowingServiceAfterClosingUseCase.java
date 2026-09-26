package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.parking.ExternalTowingService;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.shared.ParkingOpeningHours;

/**
 * Vehicles left on the parking after closing are not released automatically. A tow truck is called
 * and the parking spot becomes free only when the tow truck confirms that the vehicle was towed
 * (see {@link RemoveOccupationByForceUseCase} with reason VEHICLE_TOWED).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallingTowingServiceAfterClosingUseCase {

  private final DateProvider dateProvider;
  private final ParkingFacade parkingFacade;
  private final ExternalTowingService externalTowingService;

  public int run() {
    if (!ParkingOpeningHours.isTechnicalBreak(dateProvider.now())) {
      log.debug("towing service can be called only during technical break");
      return 0;
    }
    var occupationsToTow = parkingFacade.findAllOccupations();
    if (occupationsToTow.isEmpty()) {
      log.debug("no vehicles left after closing, towing service is not needed");
      return 0;
    }
    log.debug("calling towing service for {} vehicles left after closing", occupationsToTow.size());
    externalTowingService.call(occupationsToTow);
    return occupationsToTow.size();
  }

}
