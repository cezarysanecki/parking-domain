package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingSpotConfig {

  private final ParkingSpotRepository parkingSpotRepository;
  private final EventPublisher eventPublisher;

  @Bean
  ParkingSpotFacade parkingSpotFacade() {
    return new ParkingSpotFacade(
        parkingSpotRepository,
        eventPublisher);
  }

}
