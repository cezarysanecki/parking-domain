package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
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
  ParkingSpotRequestsRepository parkingSpotReservationRequestsRepository
  
  def "persistence of parking spot reservation requests in real database should work"() {
    given:
      parkingSpotReservationRequestsRepository.createUsing(parkingSpotId, ParkingSpotCapacity.of(4))
    and:
      def firstReservation = ReservationId.newOne()
      def secondReservation = ReservationId.newOne()
    
    when:
      parkingSpotReservationRequestsRepository.publish(createReservationRequestOnPartOfParkingSpot(
          parkingSpotId, firstReservation, VehicleSize.of(2)))
    and:
      parkingSpotReservationRequestsRepository.publish(createReservationRequestOnPartOfParkingSpot(
          parkingSpotId, secondReservation, VehicleSize.of(2)))
    then:
      parkingSpotReservationRequestsShouldBeFoundInDatabaseCannotHandlingMoreReservations(parkingSpotId)
    
    when:
      parkingSpotReservationRequestsRepository.publish(cancelParkingSpotReservationRequest(parkingSpotId, firstReservation))
    and:
      parkingSpotReservationRequestsRepository.publish(cancelParkingSpotReservationRequest(parkingSpotId, secondReservation))
    then:
      parkingSpotReservationRequestsShouldBeFoundInDatabaseBeingFree(parkingSpotId)
  }
  
  private RequestForPartOfParkingSpotStored createReservationRequestOnPartOfParkingSpot(
      ParkingSpotId parkingSpotId, ReservationId reservationId, VehicleSize vehicleSize) {
    return new RequestForPartOfParkingSpotStored(parkingSpotId, reservationId, vehicleSize)
  }
  
  private ParkingSpotRequestCancelled cancelParkingSpotReservationRequest(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    return new ParkingSpotRequestCancelled(parkingSpotId, reservationId)
  }
  
  private void parkingSpotReservationRequestsShouldBeFoundInDatabaseCannotHandlingMoreReservations(ParkingSpotId parkingSpotId) {
    def parkingSpotReservationRequests = loadPersistedParkingSpotReservationRequests(parkingSpotId)
    assert parkingSpotReservationRequests.cannotHandleMore()
  }
  
  private void parkingSpotReservationRequestsShouldBeFoundInDatabaseBeingFree(ParkingSpotId parkingSpotId) {
    def parkingSpotReservationRequests = loadPersistedParkingSpotReservationRequests(parkingSpotId)
    assert parkingSpotReservationRequests.isFree()
  }
  
  ParkingSpotRequests loadPersistedParkingSpotReservationRequests(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotRequests> loaded = parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
    ParkingSpotRequests parkingSpotReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotReservations
  }
  
}
