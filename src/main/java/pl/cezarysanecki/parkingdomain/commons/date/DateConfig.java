package pl.cezarysanecki.parkingdomain.commons.date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateConfig {

    @Bean
    DateProvider dateProvider() {
        return new ProductionDateProvider();
    }

}
