package pl.cezarysanecki.parkingdomain.parking.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.reservation.ReservationFacade;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

@Component
@RequiredArgsConstructor
public class OccupyUsingReservationUseCase {

  private final ReservationFacade reservationFacade;
  private final ParkingFacade parkingFacade;

  @Transactional
  public void run(OccupantId occupantId, ReservationId reservationId) {
    reservationFacade.useReservationFor(reservationId, reservation -> {
      parkingFacade.occupy(
          occupantId,
          reservation.parkingSpotId(),
          reservation.spotUnits()
      );
    });
  }

}
