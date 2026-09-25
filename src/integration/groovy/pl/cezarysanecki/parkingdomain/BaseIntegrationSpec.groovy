package pl.cezarysanecki.parkingdomain

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

/**
 * Base for specs running against the production (JOOQ + Liquibase) setup with a real Postgres.
 * One container per JVM (singleton container pattern) - stopped by Testcontainers' Ryuk after the run.
 */
@SpringBootTest
@ActiveProfiles("integration")
abstract class BaseIntegrationSpec extends Specification {

  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16.4")

  static {
    POSTGRES.start()
  }

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl)
    registry.add("spring.datasource.username", POSTGRES::getUsername)
    registry.add("spring.datasource.password", POSTGRES::getPassword)
  }

}
