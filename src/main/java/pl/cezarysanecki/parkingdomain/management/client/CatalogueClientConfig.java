package pl.cezarysanecki.parkingdomain.management.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
public class CatalogueClientConfig {

  private final EventPublisher eventPublisher;

  @Bean
  RegisteringClient registeringClient(CatalogueClientDatabase catalogueParkingSpotDatabase) {
    return new RegisteringClient(catalogueParkingSpotDatabase, eventPublisher);
  }

  @Bean
  @Profile("local")
  CatalogueClientDatabase catalogueClientDatabase() {
    return new CatalogueClientDatabase.InMemoryCatalogueClientDatabase();
  }

}
