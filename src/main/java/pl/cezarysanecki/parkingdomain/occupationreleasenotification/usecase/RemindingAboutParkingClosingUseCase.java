package pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.OccupationReleaseNotificationFacade;
import pl.cezarysanecki.parkingdomain.shared.ParkingOpeningHours;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemindingAboutParkingClosingUseCase {

  private final DateProvider dateProvider;
  private final OccupationReleaseNotificationFacade occupationReleaseNotificationFacade;

  @Transactional
  public int run() {
    if (!ParkingOpeningHours.isTimeToRemindAboutReleasing(dateProvider.now())) {
      log.debug("it is not time to remind about closing parking");
      return 0;
    }
    return occupationReleaseNotificationFacade.remindAboutClosing();
  }

}
