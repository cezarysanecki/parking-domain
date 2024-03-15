package pl.cezarysanecki.parkingdomain.commons.date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DateConfig {

    @Profile("!local")
    @Bean
    ProductionDateProvider productionDateProvider() {
        return new ProductionDateProvider();
    }

    @Profile("local")
    @Bean
    LocalDateProvider localDateProvider() {
        return new LocalDateProvider();
    }

}
