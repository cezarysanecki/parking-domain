package pl.cezarysanecki.parkingdomain.reserving.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsFixture.clientReservationsWithReservation

class CancellingReservationRequestAfterFailedReservationTest extends Specification {
  
  ClientReservationsRepository clientReservationsRepository = Mock()
  
  @Subject
  ParkingSpotReservationsEventHandler parkingSpotReservationsEventHandler = new ParkingSpotReservationsEventHandler(
      new CancellingReservationRequest(clientReservationsRepository))
  
  def "cancel client reservation request when reserving parking spot fails"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      def clientReservations = clientReservationsWithReservation(reservationId)
    and:
      clientReservationsRepository.findBy(reservationId) >> Option.of(clientReservations)
    
    when:
      parkingSpotReservationsEventHandler.handle(
          new ParkingSpotReservationEvent.ParkingSpotReservationFailed(ParkingSpotId.newOne(), reservationId, "any reason"))
    
    then:
      1 * clientReservationsRepository.publish({
        it.clientId == clientReservations.clientId
            && it.reservationId == reservationId
      } as ReservationRequestCancelled)
  }
  
}
