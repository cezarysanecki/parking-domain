package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.RequestingReservationConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored

@ActiveProfiles("local")
@SpringBootTest(classes = [RequestingReservationConfig.class])
class ParkingSpotReservationRequestsDatabaseIT extends Specification {
  
  def parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpotReservationRequestsRepository parkingSpotRequestsRepository
  
  def "persistence of parking spot requests in real database should work"() {
    given:
      parkingSpotRequestsRepository.createUsing(parkingSpotId, ParkingSpotCapacity.of(4))
    and:
      def firstRequest = ReservationRequestId.newOne()
      def secondRequest = ReservationRequestId.newOne()
    
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
      ParkingSpotId parkingSpotId, ReservationRequestId requestId, SpotUnits vehicleSize) {
    return new RequestForPartOfParkingSpotStored(parkingSpotId, requestId, vehicleSize)
  }
  
  private static ParkingSpotRequestCancelled cancelParkingSpotRequest(ParkingSpotId parkingSpotId, ReservationRequestId requestId) {
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
  
  private ParkingSpotReservationRequests loadPersistedParkingSpotRequests(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotReservationRequests> loaded = parkingSpotRequestsRepository.findBy(parkingSpotId)
    ParkingSpotReservationRequests parkingSpotRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotRequests
  }
  
}
