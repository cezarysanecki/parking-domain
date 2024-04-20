package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture.ParkingSpotReservationRequestsConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequests
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotReservationRequestsConfig.class])
class ParkingSpotReservationRequestsDatabaseIT extends Specification {
  
  def parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository
  
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
  
  private ReservationRequestForPartOfParkingSpotStored createReservationRequestOnPartOfParkingSpot(
      ParkingSpotId parkingSpotId, ReservationId reservationId, VehicleSize vehicleSize) {
    return new ReservationRequestForPartOfParkingSpotStored(parkingSpotId, reservationId, vehicleSize)
  }
  
  private ParkingSpotReservationRequestCancelled cancelParkingSpotReservationRequest(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    return new ParkingSpotReservationRequestCancelled(parkingSpotId, reservationId)
  }
  
  private void parkingSpotReservationRequestsShouldBeFoundInDatabaseCannotHandlingMoreReservations(ParkingSpotId parkingSpotId) {
    def parkingSpotReservationRequests = loadPersistedParkingSpotReservationRequests(parkingSpotId)
    assert parkingSpotReservationRequests.cannotHandleMore()
  }
  
  private void parkingSpotReservationRequestsShouldBeFoundInDatabaseBeingFree(ParkingSpotId parkingSpotId) {
    def parkingSpotReservationRequests = loadPersistedParkingSpotReservationRequests(parkingSpotId)
    assert parkingSpotReservationRequests.isFree()
  }
  
  ParkingSpotReservationRequests loadPersistedParkingSpotReservationRequests(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotReservationRequests> loaded = parkingSpotReservationRequestsRepository.findBy(parkingSpotId)
    ParkingSpotReservationRequests parkingSpotReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotReservations
  }
  
}
