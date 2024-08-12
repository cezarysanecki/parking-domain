package pl.cezarysanecki.parkingdomain.requesting;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.InMemoryOccupationRepository;
import pl.cezarysanecki.parkingdomain.parking.InMemoryParkingSpotRepository;

@Configuration
@RequiredArgsConstructor
class RequestingConfig {

  private final RequestableSectionRepository requestableSectionRepository;
  private final RequesterRepository requesterRepository;
  private final RequestRepository requestRepository;
  private final EventPublisher eventPublisher;

  @Bean
  RequestingFacade requestingFacade() {
    return new RequestingFacade(
        requestableSectionRepository,
        requesterRepository,
        requestRepository,
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

  @Bean
  InMemoryOccupationRepository inMemoryOccupationRepository() {
    return new InMemoryOccupationRepository();
  }

}
