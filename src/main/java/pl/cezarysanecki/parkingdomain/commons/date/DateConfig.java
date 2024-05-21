package pl.cezarysanecki.parkingdomain.commons.date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DateConfig {

    @Bean
    @Profile("!local")
    ProductionDateProvider productionDateProvider() {
        return new ProductionDateProvider();
    }

}
