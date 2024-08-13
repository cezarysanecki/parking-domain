package pl.cezarysanecki.parkingdomain.management.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ClientConfig {

  private final EventPublisher eventPublisher;
  private final ClientRepository clientRepository;

  @Bean
  ClientFacade clientFacade() {
    return new ClientFacade(
        clientRepository,
        eventPublisher);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalClientConfig {

  @Bean
  InMemoryClientRepository inMemoryClientRepository() {
    return new InMemoryClientRepository();
  }

}
