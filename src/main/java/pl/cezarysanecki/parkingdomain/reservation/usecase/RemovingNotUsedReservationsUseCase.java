package pl.cezarysanecki.parkingdomain.reservation.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.reservation.ReservationFacade;
import pl.cezarysanecki.parkingdomain.shared.BusinessDateProvider;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RemovingNotUsedReservationsUseCase {

  private final BusinessDateProvider businessDateProvider;
  private final ReservationFacade reservationFacade;

  @Transactional
  public void run() {
    Instant date = businessDateProvider.provideDateForMarkingReservationsAsNotUsed();
    reservationFacade.removeNotUsedReservationsFor(date);
  }

}
