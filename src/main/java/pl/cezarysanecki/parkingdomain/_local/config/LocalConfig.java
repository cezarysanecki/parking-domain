package pl.cezarysanecki.parkingdomain._local.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

@Slf4j
@Configuration
@Profile("local")
public class LocalConfig {

  @Bean
  LocalDateProvider dateProvider() {
    return new LocalDateProvider();
  }

}
