package pl.cezarysanecki.parkingdomain._global;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
class DatabaseConfig {

  private final DataSource dataSource;

  @Bean
  DSLContext jooqDsl() {
    Settings settings = new Settings()
        .withRenderQuotedNames(RenderQuotedNames.ALWAYS)
        .withRenderNameCase(RenderNameCase.LOWER);
    DataSourceConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(
        new TransactionAwareDataSourceProxy(dataSource));
    return new DefaultDSLContext(new DefaultConfiguration()
        .set(dataSource)
        .set(settings)
        .set(SQLDialect.POSTGRES)
        .set(dataSourceConnectionProvider));
  }

}
