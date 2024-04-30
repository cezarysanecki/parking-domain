package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForAnyRequests
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class MakingRequestForWholeParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  RequestId requestId = RequestId.newOne()
  
  ParkingSpotRequestsRepository parkingSpotRequestsRepository = Mock()
  
  @Subject
  StoringParkingSpotRequestEventHandler storingParkingSpotRequestEventHandler = new StoringParkingSpotRequestEventHandler(parkingSpotRequestsRepository)
  
  def "make request for whole parking spot when client made a request for it"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutRequests()
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotRequests.parkingSpotId) >> Option.of(parkingSpotRequests)
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForWholeParkingSpotMade(
          clientId, requestId, parkingSpotRequests.parkingSpotId))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotRequests.parkingSpotId
            && it.requestId == requestId
      } as RequestForWholeParkingSpotStored)
  }
  
  def "reject making request for whole parking spot when there is another request already"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutPlaceForAnyRequests(RequestId.newOne())
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotRequests.parkingSpotId) >> Option.of(parkingSpotRequests)
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForWholeParkingSpotMade(
          clientId, requestId, parkingSpotRequests.parkingSpotId))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotRequests.parkingSpotId
            && it.requestId == requestId
      } as StoringParkingSpotRequestFailed)
  }
  
  def "reject making request for whole parking spot when parking spot requests does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForWholeParkingSpotMade(
          clientId, requestId, parkingSpotId))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.requestId == requestId
      } as StoringParkingSpotRequestFailed)
  }
  
}
