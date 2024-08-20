package pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.OccupationReleaseNotificationFacade;
import pl.cezarysanecki.parkingdomain.shared.BusinessDateProvider;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class NotifyingAboutReleasingOccupationUseCase {

  private final BusinessDateProvider businessDateProvider;
  private final OccupationReleaseNotificationFacade occupationReleaseNotificationFacade;

  @Transactional
  public int run() {
    Instant date = businessDateProvider.provideDateForReleasingOccupationBecauseOfReservations();
    return occupationReleaseNotificationFacade.notifyToReleaseFor(date);
  }

}
