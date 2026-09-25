package pl.cezarysanecki.parkingdomain.fee

import pl.cezarysanecki.parkingdomain.fee.api.FeeType
import pl.cezarysanecki.parkingdomain.fee.api.Money
import spock.lang.Specification

class PriceListSpec extends Specification {

  def "returns price configured for fee type"() {
    given:
      def priceList = new PriceList([(FeeType.NOT_USED_RESERVATION): Money.of("50", "USD")])

    expect:
      priceList.priceFor(FeeType.NOT_USED_RESERVATION) == Money.of("50.00", "USD")
  }

  def "cannot create price list without price for every fee type"() {
    when:
      new PriceList([:])

    then:
      def e = thrown(IllegalStateException)
      e.message.contains("NOT_USED_RESERVATION")
  }

}
