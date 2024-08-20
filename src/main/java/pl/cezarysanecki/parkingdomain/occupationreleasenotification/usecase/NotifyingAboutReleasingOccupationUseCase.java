package pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.OccupationReleaseNotificationFacade;
import pl.cezarysanecki.parkingdomain.reservation.ReservationFacade;
import pl.cezarysanecki.parkingdomain.shared.BusinessDateProvider;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class NotifyingAboutReleasingOccupationUseCase {

  private final BusinessDateProvider businessDateProvider;
  private final OccupationReleaseNotificationFacade occupationReleaseNotificationFacade;

  @Transactional
  public void run() {
    Instant date = businessDateProvider.provideDateForReleasingOccupationBecauseOfReservations();
    occupationReleaseNotificationFacade.notifyToReleaseFor(date);
  }

}
