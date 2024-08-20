package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ReleasedOccupation;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConvertingOccupationUsingReservationUseCase {

  private final ParkingFacade parkingFacade;
  private final OccupyUsingReservationUseCase occupyUsingReservationUseCase;

  public Optional<OccupationId> run(OccupationId occupationId, ReservationId reservationId) {
    Optional<ReleasedOccupation> potentiallyReleasedOccupation = parkingFacade.release(occupationId);
    if (potentiallyReleasedOccupation.isEmpty()) {
      return Optional.empty();
    }
    ReleasedOccupation releasedOccupation = potentiallyReleasedOccupation.get();

    return occupyUsingReservationUseCase.run(releasedOccupation.occupantId(), reservationId);
  }

}
