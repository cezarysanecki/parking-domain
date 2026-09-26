package pl.cezarysanecki.parkingdomain.fee.api

import spock.lang.Specification

class MoneySpec extends Specification {

  def "amount is normalized to two decimal places for USD"() {
    expect:
      Money.of("50", "USD").amount() == new BigDecimal("50.00")
      Money.of("50", "USD").amount().scale() == 2
  }

  def "money with the same value and currency is equal regardless of written scale"() {
    expect:
      Money.of("50", "USD") == Money.of("50.00", "USD")
  }

  def "cannot create money with #description"() {
    when:
      Money.of(amount, "USD")

    then:
      def e = thrown(IllegalArgumentException)
      e.message.contains(message)

    where:
      description               | amount   || message
      "negative amount"         | "-0.01"  || "cannot be negative"
      "too many decimal places" | "50.001" || "too many decimal places"
  }

  def "is printed as amount and currency code"() {
    expect:
      Money.of("50", "USD").toString() == "50.00 USD"
  }

}
