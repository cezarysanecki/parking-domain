package pl.cezarysanecki.parkingdomain.requesting;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

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
  InMemoryRequestableSectionRepository inMemoryRequestableSectionRepository() {
    return new InMemoryRequestableSectionRepository();
  }

  @Bean
  InMemoryRequesterRepository inMemoryRequesterRepository() {
    return new InMemoryRequesterRepository();
  }

  @Bean
  InMemoryRequestRepository inMemoryRequestRepository() {
    return new InMemoryRequestRepository();
  }

}
