package pl.cezarysanecki.parkingdomain.availability.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
class SpotConfiguration {

    @Bean
    @Profile("local")
    InMemorySpotRepository inMemorySpotRepository() {
        return new InMemorySpotRepository();
    }

    @Bean
    @Profile("!local")
    JdbcSpotRepository jdbcSpotRepository() {
        return new JdbcSpotRepository();
    }

}
