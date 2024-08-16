package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;

@Component
@RequiredArgsConstructor
public class RemoveOccupationByForceUseCase {

  private final ParkingFacade parkingFacade;
  private final EventPublisher eventPublisher;

  public boolean run(OccupationId occupationId, ParkingSpotForceReleased.Reason reason) {
    var result = parkingFacade.release(occupationId);

    result.ifPresent(releasedOccupation -> eventPublisher.publish(new ParkingSpotForceReleased(
        releasedOccupation.occupationId(),
        releasedOccupation.occupantId(),
        releasedOccupation.parkingSpotId(),
        releasedOccupation.spotUnits(),
        reason
    )));

    return result.isPresent();
  }

}
