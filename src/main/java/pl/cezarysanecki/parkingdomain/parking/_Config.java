package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingConfig {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final OccupantRepository occupantRepository;
  private final ParkingSpotReservationRepository parkingSpotReservationRepository;
  private final EventPublisher eventPublisher;

  @Bean
  ParkingFacade parkingFacade() {
    return new ParkingFacade(
        parkingRepository,
        occupationRepository,
        occupantRepository,
        eventPublisher);
  }

  @Bean
  ParkingEventHandler parkingEventHandler() {
    return new ParkingEventHandler(
        parkingRepository,
        occupantRepository,
        parkingSpotReservationRepository);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalParkingConfig {

  @Bean
  InMemoryParkingRepository inMemoryParkingRepository() {
    return new InMemoryParkingRepository();
  }

  @Bean
  InMemoryOccupationRepository inMemoryOccupationRepository() {
    return new InMemoryOccupationRepository();
  }

  @Bean
  InMemoryOccupantRepository inMemoryOccupantRepository() {
    return new InMemoryOccupantRepository();
  }

  @Bean
  InMemoryParkingSpotReservationRepository inMemoryParkingSpotReservationRepository() {
    return new InMemoryParkingSpotReservationRepository();
  }

}
