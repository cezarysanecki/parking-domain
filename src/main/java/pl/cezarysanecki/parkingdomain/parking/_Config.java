package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingConfig {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final OccupantRepository occupantRepository;
  private final ReservedOccupationRepository reservedOccupationRepository;
  private final EventPublisher eventPublisher;
  private final DateProvider dateProvider;

  @Bean
  ParkingFacade parkingFacade() {
    return new ParkingFacade(
        parkingRepository,
        occupationRepository,
        occupantRepository,
        eventPublisher,
        dateProvider);
  }

  @Bean
  ParkingEventHandler parkingEventHandler() {
    return new ParkingEventHandler(
        parkingRepository,
        occupantRepository,
        reservedOccupationRepository);
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
  InMemoryReservedOccupationRepository inMemoryReservedOccupationRepository() {
    return new InMemoryReservedOccupationRepository();
  }

}
