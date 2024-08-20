package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.notification.NotificationFacade;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class OccupationReleaseNotificationFacade {

  private final OccupationReleaseNotificationRepository occupationReleaseNotificationRepository;
  private final NotificationFacade notificationFacade;

  public void notifyToReleaseFor(Instant date) {
    List<NotificationResolver> notificationResolvers = occupationReleaseNotificationRepository.findFor(date);

    List<OccupantToNotify> occupantsToNotify = notificationResolvers.stream()
        .map(NotificationResolver::resolveOccupantsToNotify)
        .flatMap(Collection::stream)
        .toList();

    occupantsToNotify.forEach(
        occupantToNotify -> notificationFacade.notify(
            new ClientId(occupantToNotify.occupantId().value()),
            "please release parking spot with id " + occupantToNotify.parkingSpotId() + " because it has reservation at " + occupantToNotify.reservationStartDate()
        )
    );

    occupationReleaseNotificationRepository.doneFor(date);
  }

}
