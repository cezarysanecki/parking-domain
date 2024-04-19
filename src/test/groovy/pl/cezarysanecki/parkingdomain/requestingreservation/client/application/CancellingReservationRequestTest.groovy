package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static CancellingReservationRequest.Command
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithReservationRequest
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsFixture.clientWithNoReservationRequests

class CancellingReservationRequestTest extends Specification {
  
  ClientReservationRequestsRepository clientReservationRequestsRepository = Mock()
  
  @Subject
  CancellingReservationRequest cancellingReservationRequest = new CancellingReservationRequest(clientReservationRequestsRepository)
  
  def "allow to cancel client reservation request"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.of(clientWithReservationRequest(reservationId))
    
    when:
      def result = cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject cancelling client reservation request when there is no such for client"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.of(clientWithNoReservationRequests())
    
    when:
      def result = cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def "fail to cancel client reservation request when cannot find client reservations for such reservation request"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.none()
    
    when:
      def result = cancellingReservationRequest.cancelReservationRequest(new Command(reservationId))
    
    then:
      result.isFailure()
  }
  
}
