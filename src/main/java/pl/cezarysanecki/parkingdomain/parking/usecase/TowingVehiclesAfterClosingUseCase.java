package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased;
import pl.cezarysanecki.parkingdomain.shared.ParkingOpeningHours;

@Slf4j
@Component
@RequiredArgsConstructor
public class TowingVehiclesAfterClosingUseCase {

  private final DateProvider dateProvider;
  private final ParkingFacade parkingFacade;
  private final RemoveOccupationByForceUseCase removeOccupationByForceUseCase;

  public int run() {
    if (!ParkingOpeningHours.isTechnicalBreak(dateProvider.now())) {
      log.debug("vehicles can be towed only during technical break");
      return 0;
    }
    var occupations = parkingFacade.findAllOccupations();
    log.debug("towing {} vehicles left after closing", occupations.size());

    return (int) occupations.stream()
        .filter(occupationId -> removeOccupationByForceUseCase.run(
            occupationId, ParkingSpotForceReleased.Reason.PARKING_CLOSED))
        .count();
  }

}
