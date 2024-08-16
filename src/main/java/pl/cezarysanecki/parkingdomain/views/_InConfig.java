package pl.cezarysanecki.parkingdomain.views;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
class LocalViews {

  @Bean
  InMemoryViews inMemoryViews(
      @Value("${business.cleaning.number-of-drives-away-to-consider-parking-spot-dirty}") int numberOfDrivesAwayToConsiderParkingSpotDirty
  ) {
    return new InMemoryViews(
        numberOfDrivesAwayToConsiderParkingSpotDirty
    );
  }

}
