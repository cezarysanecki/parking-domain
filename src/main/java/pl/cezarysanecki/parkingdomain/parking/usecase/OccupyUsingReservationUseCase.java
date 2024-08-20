package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.reservation.ReservationFacade;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OccupyUsingReservationUseCase {

  private final ReservationFacade reservationFacade;
  private final ParkingFacade parkingFacade;

  @Transactional
  public Optional<OccupationId> run(OccupantId occupantId, ReservationId reservationId) {
    log.debug("occupying parking spot using reservation with id {}", reservationId);
    return reservationFacade.useReservationFor(reservationId, reservation -> {
          return parkingFacade.occupyUsing(
              occupantId,
              reservation.parkingSpotId(),
              reservationId
          );
        })
        .flatMap(result -> result);
  }

}
