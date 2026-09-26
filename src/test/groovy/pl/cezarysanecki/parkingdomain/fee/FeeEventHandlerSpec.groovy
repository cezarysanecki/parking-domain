package pl.cezarysanecki.parkingdomain.fee

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.reservation.ReservationsRemoved
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationOwnerId
import spock.lang.Specification

class FeeEventHandlerSpec extends Specification {

  FeeFacade feeFacade = Mock()
  FeeEventHandler handler = new FeeEventHandler(feeFacade)

  def "charges owner of every removed (not used) reservation"() {
    given:
      def firstOwner = UUID.randomUUID()
      def secondOwner = UUID.randomUUID()
      def firstReservation = new ReservationId(UUID.randomUUID())
      def secondReservation = new ReservationId(UUID.randomUUID())

    when:
      handler.handle(new ReservationsRemoved([
          new ReservationsRemoved.Entry(firstReservation, new ReservationOwnerId(firstOwner)),
          new ReservationsRemoved.Entry(secondReservation, new ReservationOwnerId(secondOwner))
      ]))

    then:
      1 * feeFacade.chargeForNotUsedReservation(new ClientId(firstOwner), firstReservation)
      1 * feeFacade.chargeForNotUsedReservation(new ClientId(secondOwner), secondReservation)
      0 * _
  }

  def "charges nobody when no reservation was removed"() {
    when:
      handler.handle(new ReservationsRemoved([]))

    then:
      0 * feeFacade._
  }

}
