package pl.cezarysanecki.parkingdomain.requestingreservation.application

import io.vavr.control.Option
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits
import pl.cezarysanecki.parkingdomain.management.client.ClientId

import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository
import spock.lang.Specification
import spock.lang.Subject

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ParkingSpotRequestEvent.StoringParkingSpotRequestFailed
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotRequestsFixture.parkingSpotWithoutPlaceForAnyRequests
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotRequestsFixture.parkingSpotWithoutRequests

class MakingRequestForPartOfParkingSpotHandlingClientRequestTest extends Specification {
  
  ClientId clientId = ClientId.newOne()
  ReservationRequestId requestId = ReservationRequestId.newOne()
  
  ParkingSpotReservationRequestsRepository parkingSpotRequestsRepository = Mock()
  
  @Subject
  StoringParkingSpotRequestEventHandler storingParkingSpotRequestEventHandler = new StoringParkingSpotRequestEventHandler(parkingSpotRequestsRepository)
  
  def "make request for part of parking spot when client made a request for it"() {
    given:
      def vehicleSize = SpotUnits.of(2)
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
      def parkingSpotRequests = parkingSpotWithoutPlaceForAnyRequests(ReservationRequestId.newOne())
    and:
      parkingSpotRequestsRepository.findBy(parkingSpotRequests.parkingSpotId) >> Option.of(parkingSpotRequests)
    
    when:
      storingParkingSpotRequestEventHandler.handle(new RequestForPartOfParkingSpotMade(
          clientId, requestId, parkingSpotRequests.parkingSpotId, SpotUnits.of(2)))
    
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
          clientId, requestId, parkingSpotId, SpotUnits.of(2)))
    
    then:
      1 * parkingSpotRequestsRepository.publish({
        it.parkingSpotId == parkingSpotId
            && it.requestId == requestId
      } as StoringParkingSpotRequestFailed)
  }
  
}
