package pl.cezarysanecki.parkingdomain.parking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ParkingSpotConfig {

    @Bean
    @Profile("local")
    ParkingSpotRepository parkingSpotRepository() {
        return new ParkingSpotRepository.InMemoryParkingSpotRepository();
    }

}

