package pl.cezarysanecki.parkingdomain.requestingreservation.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.management.client.ClientId

import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingRequestForWholeParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientRequestsFixture.clientWithNoRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientRequestsFixture.clientWithRequest

class MakingRequestForPartOfParkingSpotTest extends Specification {
  
  ReservationRequesterRepository clientRequestsRepository = Mock()
  
  @Subject
  MakingRequestForWholeParkingSpot makingRequestForWholeParkingSpot = new MakingRequestForWholeParkingSpot(clientRequestsRepository)
  
  def "allow to make client request for part of parking spot"() {
    given:
      def clientRequests = clientWithNoRequests()
    and:
      clientRequestsRepository.findBy(clientRequests.clientId) >> Option.of(clientRequests)
    
    when:
      def result = makingRequestForWholeParkingSpot.makeRequest(
          new Command(clientRequests.clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "allow to make client request for part of parking spot even when client is not present"() {
    given:
      def clientId = ClientId.newOne()
    and:
      clientRequestsRepository.findBy(clientId) >> Option.none()
    
    when:
      def result = makingRequestForWholeParkingSpot.makeRequest(
          new Command(clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject making client request for part of parking spot when client has reached limit"() {
    given:
      def clientRequests = clientWithRequest(ReservationRequestId.newOne())
    and:
      clientRequestsRepository.findBy(clientRequests.clientId) >> Option.of(clientRequests)
    
    when:
      def result = makingRequestForWholeParkingSpot.makeRequest(
          new Command(clientRequests.clientId, ParkingSpotId.newOne()))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}