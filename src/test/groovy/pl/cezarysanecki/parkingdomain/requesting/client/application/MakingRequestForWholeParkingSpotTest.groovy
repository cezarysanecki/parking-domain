package pl.cezarysanecki.parkingdomain.requesting.client.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.management.vehicle.SpotUnits
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import spock.lang.Specification
import spock.lang.Subject

import static MakingRequestForPartOfParkingSpot.Command
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsFixture.clientWithNoRequests
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsFixture.clientWithRequest

class MakingRequestForWholeParkingSpotTest extends Specification {
  
  ClientRequestsRepository clientRequestsRepository = Mock()
  
  @Subject
  MakingRequestForPartOfParkingSpot makingRequestForPartOfParkingSpot = new MakingRequestForPartOfParkingSpot(clientRequestsRepository)
  
  def "allow to make client request for whole parking spot"() {
    given:
      def clientRequests = clientWithNoRequests()
    and:
      clientRequestsRepository.findBy(clientRequests.clientId) >> Option.of(clientRequests)
    
    when:
      def result = makingRequestForPartOfParkingSpot.makeRequest(
          new Command(clientRequests.clientId, ParkingSpotId.newOne(), SpotUnits.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "allow to make client request for whole parking spot even when client is not present"() {
    given:
      def clientId = ClientId.newOne()
    and:
      clientRequestsRepository.findBy(clientId) >> Option.none()
    
    when:
      def result = makingRequestForPartOfParkingSpot.makeRequest(
          new Command(clientId, ParkingSpotId.newOne(), SpotUnits.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Success
  }
  
  def "reject making client request for whole parking spot when client has reached limit"() {
    given:
      def clientRequests = clientWithRequest(RequestId.newOne())
    and:
      clientRequestsRepository.findBy(clientRequests.clientId) >> Option.of(clientRequests)
    
    when:
      def result = makingRequestForPartOfParkingSpot.makeRequest(
          new Command(clientRequests.clientId, ParkingSpotId.newOne(), SpotUnits.of(2)))
    
    then:
      result.isSuccess()
      result.get() in Result.Rejection
  }
  
}
