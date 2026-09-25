package pl.cezarysanecki.parkingdomain.fee

import org.jooq.exception.IntegrityConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.fee.api.FeeId
import pl.cezarysanecki.parkingdomain.fee.api.FeeType
import pl.cezarysanecki.parkingdomain.fee.api.Money
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.reservation.ReservationsRemoved
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId
import pl.cezarysanecki.parkingdomain.views.ViewFeesRepository

import java.time.Instant
import java.time.temporal.ChronoUnit

class FeeIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  FeeFacade feeFacade
  @Autowired
  FeeRepository feeRepository
  @Autowired
  ViewFeesRepository viewFeesRepository
  @Autowired
  EventPublisher eventPublisher

  def "fee for not used reservation is stored in database with amount and currency from price list"() {
    given:
      def clientId = ClientId.newOne()
      def reservationId = new ReservationId(UUID.randomUUID())

    when:
      def result = feeFacade.chargeForNotUsedReservation(clientId, reservationId)

    then:
      result == Result.Success
    and:
      def fees = viewFeesRepository.queryFeesFor(clientId)
      fees.size() == 1
      with(fees.first()) {
        it.reservationId() == reservationId.value()
        it.type() == "NOT_USED_RESERVATION"
        it.amount() == new BigDecimal("50.00")
        it.amount().scale() == 2
        it.currency() == "USD"
      }
  }

  def "removed reservations event charges their owners (prod wiring)"() {
    given:
      def owner = UUID.randomUUID()
      def reservationId = new ReservationId(UUID.randomUUID())

    when:
      eventPublisher.publish(new ReservationsRemoved([
          new ReservationsRemoved.Entry(reservationId, new ReservationOwnerId(owner))]))

    then:
      viewFeesRepository.queryFeesFor(new ClientId(owner))*.reservationId() == [reservationId.value()]
  }

  def "the same not used reservation is charged only once"() {
    given:
      def clientId = ClientId.newOne()
      def reservationId = new ReservationId(UUID.randomUUID())

    when:
      def first = feeFacade.chargeForNotUsedReservation(clientId, reservationId)
      def second = feeFacade.chargeForNotUsedReservation(clientId, reservationId)

    then:
      first == Result.Success
      second == Result.Rejection
      viewFeesRepository.queryFeesFor(clientId).size() == 1
  }

  def "database rejects second fee of the same type for the same reservation"() {
    given:
      def reservationId = new ReservationId(UUID.randomUUID())
      def fee = { new Fee(FeeId.newOne(), ClientId.newOne(), reservationId, FeeType.NOT_USED_RESERVATION, Money.of("50", "USD"), Instant.now().truncatedTo(ChronoUnit.MILLIS)) }

    when:
      feeRepository.saveNew(fee())

    then:
      noExceptionThrown()

    when:
      feeRepository.saveNew(fee())

    then:
      thrown(IntegrityConstraintViolationException)
  }

}
