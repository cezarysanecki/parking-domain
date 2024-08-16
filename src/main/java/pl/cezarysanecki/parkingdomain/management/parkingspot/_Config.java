package pl.cezarysanecki.parkingdomain.management.parkingspot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingSpotConfig {

  private final EventPublisher eventPublisher;
  private final ParkingSpotRepository parkingSpotRepository;

  @Bean
  ParkingSpotFacade parkingSpotFacade() {
    return new ParkingSpotFacade(
        parkingSpotRepository,
        eventPublisher);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalParkingSpotConfig {

  @Bean
  InMemoryParkingSpotRepository inMemoryParkingSpotRepository() {
    return new InMemoryParkingSpotRepository();
  }

}
