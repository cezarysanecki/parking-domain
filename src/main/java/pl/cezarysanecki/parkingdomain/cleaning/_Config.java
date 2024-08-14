package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
class CleaningConfig {

  @Bean
  CleaningFacade callingExternalCleaningServicePolicy(
      CleaningRepository cleaningRepository,
      ExternalCleaningService externalCleaningService,
      @Value("${business.cleaning.number-of-drives-away-to-consider-parking-spot-dirty}") int numberOfDrivesAwayToConsiderParkingSpotDirty
  ) {
    return new CleaningFacade(
        cleaningRepository,
        externalCleaningService,
        numberOfDrivesAwayToConsiderParkingSpotDirty);
  }

  @Bean
  CleaningEventHandler cleaningEventHandler(
      CleaningRepository cleaningRepository
  ) {
    return new CleaningEventHandler(cleaningRepository);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalCleaningConfig {

  @Bean
  InMemoryCleaningRepository cleaningRepository() {
    return new InMemoryCleaningRepository();
  }

}
