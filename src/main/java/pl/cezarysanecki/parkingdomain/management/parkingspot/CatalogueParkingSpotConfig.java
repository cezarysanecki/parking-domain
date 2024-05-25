package pl.cezarysanecki.parkingdomain.management.parkingspot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
public class CatalogueParkingSpotConfig {

  private final EventPublisher eventPublisher;

  @Bean
  AddingParkingSpot addingParkingSpot(CatalogueParkingSpotDatabase catalogueParkingSpotDatabase) {
    return new AddingParkingSpot(catalogueParkingSpotDatabase, eventPublisher);
  }

  @Bean
  @Profile("local")
  CatalogueParkingSpotDatabase catalogueParkingSpotDatabase() {
    return new CatalogueParkingSpotDatabase.InMemoryCatalogueParkingSpotDatabase();
  }

}
