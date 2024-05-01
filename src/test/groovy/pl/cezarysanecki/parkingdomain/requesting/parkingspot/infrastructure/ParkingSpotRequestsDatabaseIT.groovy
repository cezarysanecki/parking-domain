package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.SpotUnits
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture.ParkingSpotRequestsConfig
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequests
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotRequestsConfig.class])
class ParkingSpotRequestsDatabaseIT extends Specification {
  
  def parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpotRequestsRepository parkingSpotRequestsRepository
  
  def "persistence of parking spot requests in real database should work"() {
    given:
      parkingSpotRequestsRepository.createUsing(parkingSpotId, ParkingSpotCapacity.of(4))
    and:
      def firstRequest = RequestId.newOne()
      def secondRequest = RequestId.newOne()
    
    when:
      parkingSpotRequestsRepository.publish(makeRequestOnPartOfParkingSpot(
          parkingSpotId, firstRequest, SpotUnits.of(2)))
    and:
      parkingSpotRequestsRepository.publish(makeRequestOnPartOfParkingSpot(
          parkingSpotId, secondRequest, SpotUnits.of(2)))
    then:
      parkingSpotRequestsShouldBeFoundInDatabaseCannotHandlingMoreRequests(parkingSpotId)
    
    when:
      parkingSpotRequestsRepository.publish(cancelParkingSpotRequest(parkingSpotId, firstRequest))
    and:
      parkingSpotRequestsRepository.publish(cancelParkingSpotRequest(parkingSpotId, secondRequest))
    then:
      parkingSpotRequestsShouldBeFoundInDatabaseBeingFree(parkingSpotId)
  }
  
  private static RequestForPartOfParkingSpotStored makeRequestOnPartOfParkingSpot(
      ParkingSpotId parkingSpotId, RequestId requestId, SpotUnits vehicleSize) {
    return new RequestForPartOfParkingSpotStored(parkingSpotId, requestId, vehicleSize)
  }
  
  private static ParkingSpotRequestCancelled cancelParkingSpotRequest(ParkingSpotId parkingSpotId, RequestId requestId) {
    return new ParkingSpotRequestCancelled(parkingSpotId, requestId)
  }
  
  private void parkingSpotRequestsShouldBeFoundInDatabaseCannotHandlingMoreRequests(ParkingSpotId parkingSpotId) {
    def parkingSpotRequests = loadPersistedParkingSpotRequests(parkingSpotId)
    assert parkingSpotRequests.cannotHandleMore()
  }
  
  private void parkingSpotRequestsShouldBeFoundInDatabaseBeingFree(ParkingSpotId parkingSpotId) {
    def parkingSpotRequests = loadPersistedParkingSpotRequests(parkingSpotId)
    assert parkingSpotRequests.isFree()
  }
  
  private ParkingSpotRequests loadPersistedParkingSpotRequests(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotRequests> loaded = parkingSpotRequestsRepository.findBy(parkingSpotId)
    ParkingSpotRequests parkingSpotRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotRequests
  }
  
}
