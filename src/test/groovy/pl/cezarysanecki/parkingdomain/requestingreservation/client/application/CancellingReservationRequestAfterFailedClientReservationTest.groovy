package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientReservationsWithReservation

class CancellingReservationRequestAfterFailedClientReservationTest extends Specification {
  
  ClientReservationRequestsRepository clientReservationsRepository = Mock()
  
  @Subject
  CancellingReservationRequestEventHandler parkingSpotReservationsEventHandler = new CancellingReservationRequestEventHandler(
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
          new ParkingSpotReservationRequestEvent.StoringParkingSpotReservationRequestFailed(ParkingSpotId.newOne(), reservationId, "any reason"))
    
    then:
      1 * clientReservationsRepository.publish({
        it.clientId == clientReservations.clientId
            && it.reservationId == reservationId
      } as ReservationRequestCancelled)
  }
  
}
