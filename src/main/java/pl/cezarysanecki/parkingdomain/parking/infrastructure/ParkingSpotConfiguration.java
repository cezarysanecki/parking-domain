package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
class ParkingSpotConfiguration {

    @Bean
    @Profile("local")
    InMemoryParkingSpotRepository inMemoryParkingSpotRepository() {
        return new InMemoryParkingSpotRepository();
    }

    @Bean
    @Profile("!local")
    JdbcParkingSpotRepository jdbcParkingSpotRepository() {
        return new JdbcParkingSpotRepository();
    }

}
