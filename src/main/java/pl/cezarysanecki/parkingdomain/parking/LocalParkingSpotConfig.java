package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalParkingSpotConfig {

  @Bean
  InMemoryParkingSpotRepository inMemoryParkingSpotRepository() {
    return new InMemoryParkingSpotRepository();
  }

}
