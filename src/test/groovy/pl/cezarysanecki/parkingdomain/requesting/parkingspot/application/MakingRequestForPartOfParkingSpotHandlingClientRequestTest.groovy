package pl.cezarysanecki.parkingdomain.requesting.parkingspot.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize
import pl.cezarysanecki.parkingdomain.catalogue.client.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForAnyRequests
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class MakingRequestForPartOfParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  RequestId requestId = RequestId.newOne()
  
  ParkingSpotRequestsRepository parkingSpotRequestsRepository = Mock()
  
  @Subject
  StoringParkingSpotRequestEventHandler storingParkingSpotRequestEventHandler = new StoringParkingSpotRequestEventHandler(parkingSpotRequestsRepository)
  
  def "make request for part of parking spot when client made a request for it"() {
    given:
      def vehicleSize = VehicleSize.of(2)
    and:
      def parkingSpotRequests = parkingSpotWithoutRequests()
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotRequests.parkingSpotId) >> Option.of(parkingSpotRequests)
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, requestId, parkingSpotRequests.parkingSpotId, vehicleSize))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotRequests.parkingSpotId
            && it.requestId == requestId
            && it.vehicleSize == vehicleSize
      } as RequestForPartOfParkingSpotStored)
  }
  
  def "reject making request for part of parking spot when client made a request for it when there is not enough place"() {
    given:
      def parkingSpotRequests = parkingSpotWithoutPlaceForAnyRequests(RequestId.newOne())
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotRequests.parkingSpotId) >> Option.of(parkingSpotRequests)
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, requestId, parkingSpotRequests.parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotRequests.parkingSpotId
            && it.requestId == requestId
      } as StoringParkingSpotRequestFailed)
  }
  
  def "reject making request for part of parking spot when client made a request for it when parking spot requests does not exist"() {
    given:
      def parkingSpotId = ParkingSpotId.newOne()
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotId) >> Option.of()
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, requestId, parkingSpotId, VehicleSize.of(2)))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.requestId == requestId
      } as StoringParkingSpotRequestFailed)
  }
  
}
