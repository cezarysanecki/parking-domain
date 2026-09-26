package pl.cezarysanecki.parkingdomain.fee;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.fee.api.Money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties(FeeConfig.PriceListProperties.class)
class FeeConfig {

  @Bean
  PriceList priceList(PriceListProperties properties) {
    Map<FeeType, Money> prices = new EnumMap<>(FeeType.class);
    Objects.requireNonNullElse(properties.prices(), Map.<FeeType, BigDecimal>of())
        .forEach((type, amount) -> prices.put(type, new Money(amount, properties.currency())));
    return new PriceList(prices);
  }

  @Bean
  FeeFacade feeFacade(
      FeeRepository feeRepository,
      PriceList priceList,
      DateProvider dateProvider
  ) {
    return new FeeFacade(feeRepository, priceList, dateProvider);
  }

  @Bean
  FeeEventHandler feeEventHandler(FeeFacade feeFacade) {
    return new FeeEventHandler(feeFacade);
  }

  @ConfigurationProperties(prefix = "business.fee.price-list")
  record PriceListProperties(
      Currency currency,
      Map<FeeType, BigDecimal> prices
  ) {
  }

}

@Profile("local")
@Configuration
class LocalFeeConfig {

  @Bean
  InMemoryFeeRepository inMemoryFeeRepository() {
    return new InMemoryFeeRepository();
  }

}
