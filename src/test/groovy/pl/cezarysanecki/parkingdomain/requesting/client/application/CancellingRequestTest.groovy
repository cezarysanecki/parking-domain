package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification
import spock.lang.Subject

import static CancellingRequest.Command
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithRequest
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientReservationsFixture.clientWithNoRequests

class CancellingRequestTest extends Specification {
  
  ClientRequestsRepository clientReservationRequestsRepository = Mock()
  
  @Subject
  CancellingRequest cancellingReservationRequest = new CancellingRequest(clientReservationRequestsRepository)
  
  def "allow to cancel client reservation request"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.of(clientWithRequest(reservationId))
    
    when:
      def result = cancellingReservationRequest.cancelRequest(new Command(reservationId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject cancelling client reservation request when there is no such for client"() {
    given:
      def reservationId = ReservationId.newOne()
    and:
      clientReservationRequestsRepository.findBy(reservationId) >> Option.of(clientWithNoRequests())
    
    when:
      def result = cancellingReservationRequest.cancelRequest(new Command(reservationId))
    
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
      def result = cancellingReservationRequest.cancelRequest(new Command(reservationId))
    
    then:
      result.isFailure()
  }
  
}
