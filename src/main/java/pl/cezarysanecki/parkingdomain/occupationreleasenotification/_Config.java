package pl.cezarysanecki.parkingdomain.occupationreleasenotification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.notification.NotificationFacade;

@Configuration
@RequiredArgsConstructor
class OccupationReleaseNotificationConfig {

  private final NotificationFacade notificationFacade;
  private final OccupationReleaseNotificationRepository occupationReleaseNotificationRepository;

  @Bean
  OccupationReleaseNotificationFacade occupationReleaseNotificationFacade() {
    return new OccupationReleaseNotificationFacade(
        occupationReleaseNotificationRepository,
        notificationFacade);
  }

  @Bean
  OccupationReleaseNotificationEventListener occupationReleaseNotificationEventListener() {
    return new OccupationReleaseNotificationEventListener(occupationReleaseNotificationRepository);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalOccupationReleaseNotificationConfig {

  @Bean
  InMemoryOccupationReleaseNotificationRepository inMemoryOccupationReleaseNotificationRepository() {
    return new InMemoryOccupationReleaseNotificationRepository();
  }

}
