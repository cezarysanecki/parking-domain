package pl.cezarysanecki.parkingdomain.commons.date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local")
@Configuration
public class DateConfig {

  @Bean
  ProductionDateProvider productionDateProvider() {
    return new ProductionDateProvider();
  }

}
