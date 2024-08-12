package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingSpotConfig {

  private final ParkingSpotRepository parkingSpotRepository;
  private final OccupationRepository occupationRepository;
  private final EventPublisher eventPublisher;

  @Bean
  ParkingSpotFacade parkingSpotFacade() {
    return new ParkingSpotFacade(
        parkingSpotRepository,
        occupationRepository,
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

  @Bean
  InMemoryOccupationRepository inMemoryOccupationRepository() {
    return new InMemoryOccupationRepository();
  }

}
