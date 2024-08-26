package pl.cezarysanecki.parkingdomain._global;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
class DatabaseConfig {

  private DataSource dataSource;

  @Bean
  DSLContext jooqDsl() {
    return new DefaultDSLContext(new DefaultConfiguration().set(dataSource));
  }

}
