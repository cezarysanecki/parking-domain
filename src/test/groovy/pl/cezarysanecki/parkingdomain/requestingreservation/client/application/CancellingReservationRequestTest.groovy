package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientRequestsFixture.clientWithNoRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientRequestsFixture.clientWithRequest

class CancellingReservationRequestTest extends Specification {
  
  ReservationRequesterRepository clientRequestsRepository = Mock()
  
  @Subject
  CancellingReservationRequest cancellingRequest = new CancellingReservationRequest(clientRequestsRepository)
  
  def "allow to cancel client request"() {
    given:
      def requestId = ReservationRequestId.newOne()
    and:
      clientRequestsRepository.findBy(requestId) >> Option.of(clientWithRequest(requestId))
    
    when:
      def result = cancellingRequest.cancelRequest(new Command(requestId))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject cancelling client request when there is no such for client"() {
    given:
      def requestId = ReservationRequestId.newOne()
    and:
      clientRequestsRepository.findBy(requestId) >> Option.of(clientWithNoRequests())
    
    when:
      def result = cancellingRequest.cancelRequest(new Command(requestId))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
  def "fail to cancel client request when cannot find client requests for such request"() {
    given:
      def requestId = ReservationRequestId.newOne()
    and:
      clientRequestsRepository.findBy(requestId) >> Option.none()
    
    when:
      def result = cancellingRequest.cancelRequest(new Command(requestId))
    
    then:
      result.isFailure()
  }
  
}
