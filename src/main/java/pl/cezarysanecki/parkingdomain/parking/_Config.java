package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingSpotConfig {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final EventPublisher eventPublisher;

  @Bean
  ParkingFacade parkingFacade() {
    return new ParkingFacade(
        parkingRepository,
        occupationRepository,
        eventPublisher);
  }

  @Bean
  ParkingEventHandler parkingEventHandler() {
    return new ParkingEventHandler(parkingRepository);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalParkingSpotConfig {

  @Bean
  InMemoryParkingRepository inMemoryParkingRepository() {
    return new InMemoryParkingRepository();
  }

  @Bean
  InMemoryOccupationRepository inMemoryOccupationRepository() {
    return new InMemoryOccupationRepository();
  }

}
