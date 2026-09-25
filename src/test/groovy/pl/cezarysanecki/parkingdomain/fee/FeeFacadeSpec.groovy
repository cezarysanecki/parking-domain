package pl.cezarysanecki.parkingdomain.fee

import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider
import pl.cezarysanecki.parkingdomain.fee.api.FeeType
import pl.cezarysanecki.parkingdomain.fee.api.Money
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import spock.lang.Specification

import java.time.Instant

class FeeFacadeSpec extends Specification {

  static final Instant NOW = Instant.parse("2020-10-10T10:16:00Z")

  DateProvider dateProvider = Stub() {
    now() >> NOW
  }

  def setup() {
    InMemoryRepositories.clearAll()
  }

  def cleanup() {
    InMemoryRepositories.clearAll()
  }

  def "charges client with price from price list for not used reservation"() {
    given:
      def facade = facadeWithPrice(Money.of(price, "USD"))
      def clientId = ClientId.newOne()
      def reservationId = new ReservationId(UUID.randomUUID())

    when:
      def result = facade.chargeForNotUsedReservation(clientId, reservationId)

    then:
      result == Result.Success
    and:
      def fees = InMemoryRepositories.FEE_DATABASE.values()
      fees.size() == 1
      with(fees.first()) {
        it.clientId() == clientId
        it.reservationId() == reservationId
        it.type() == FeeType.NOT_USED_RESERVATION
        it.amount() == Money.of(price, "USD")
        it.chargedAt() == NOW
      }

    where:
      price << ["50.00", "30.00"]
  }

  def "the same not used reservation is charged only once"() {
    given:
      def facade = facadeWithPrice(Money.of("50", "USD"))
      def clientId = ClientId.newOne()
      def reservationId = new ReservationId(UUID.randomUUID())

    when:
      def first = facade.chargeForNotUsedReservation(clientId, reservationId)
    and:
      def second = facade.chargeForNotUsedReservation(clientId, reservationId)

    then:
      first == Result.Success
      second == Result.Rejection
      InMemoryRepositories.FEE_DATABASE.size() == 1
  }

  def "each not used reservation of a client is charged separately"() {
    given:
      def facade = facadeWithPrice(Money.of("50", "USD"))
      def clientId = ClientId.newOne()

    when:
      facade.chargeForNotUsedReservation(clientId, new ReservationId(UUID.randomUUID()))
      facade.chargeForNotUsedReservation(clientId, new ReservationId(UUID.randomUUID()))

    then:
      InMemoryRepositories.FEE_DATABASE.values()*.clientId() == [clientId, clientId]
  }

  private FeeFacade facadeWithPrice(Money price) {
    return new FeeFacade(
        new InMemoryFeeRepository(),
        new PriceList([(FeeType.NOT_USED_RESERVATION): price]),
        dateProvider)
  }

}
