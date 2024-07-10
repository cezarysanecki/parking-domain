package pl.cezarysanecki.parkingdomain.parking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ParkingSpotConfig {

  @Bean
  ParkingSpotFacade parkingSpotFacade() {
    return new ParkingSpotFacade();
  }

}
