package pl.cezarysanecki.parkingdomain.fee;

import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.fee.api.Money;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class PriceList {

  private final Map<FeeType, Money> prices;

  PriceList(Map<FeeType, Money> prices) {
    List<FeeType> missing = Arrays.stream(FeeType.values())
        .filter(type -> !prices.containsKey(type))
        .toList();
    if (!missing.isEmpty()) {
      throw new IllegalStateException("price list has no price for " + missing);
    }
    this.prices = new EnumMap<>(prices);
  }

  Money priceFor(FeeType type) {
    return prices.get(type);
  }

}
