package pl.cezarysanecki.parkingdomain.fee.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(
    BigDecimal amount,
    Currency currency
) {

  public Money {
    if (amount == null || currency == null) {
      throw new IllegalArgumentException("amount and currency must be provided");
    }
    if (amount.signum() < 0) {
      throw new IllegalArgumentException("amount cannot be negative");
    }
    try {
      amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    } catch (ArithmeticException e) {
      throw new IllegalArgumentException("amount " + amount + " has too many decimal places for " + currency);
    }
  }

  public static Money of(String amount, String currencyCode) {
    return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
  }

  @Override
  public String toString() {
    return amount.toPlainString() + " " + currency.getCurrencyCode();
  }

}
