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
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.PartRequestOfParkingSpotReserved

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotReservationRequestsConfig.class])
class ParkingSpotReservationRequestsDatabaseIT extends Specification {
  
  def parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpotReservationRequestsRepository parkingSpotReservationsRepository
  
  def "persistence of parking spot reservations in real database should work"() {
    given:
      parkingSpotReservationsRepository.createUsing(parkingSpotId, ParkingSpotCapacity.of(4))
    and:
      def firstReservation = ReservationId.newOne()
      def secondReservation = ReservationId.newOne()
    
    when:
      parkingSpotReservationsRepository.publish(createReservationForPartOfParkingSpot(
          parkingSpotId, firstReservation, VehicleSize.of(2)))
    and:
      parkingSpotReservationsRepository.publish(createReservationForPartOfParkingSpot(
          parkingSpotId, secondReservation, VehicleSize.of(2)))
    then:
      parkingSpotReservationsShouldBeFoundInDatabaseCannotHandlingMoreReservations(parkingSpotId)
    
    when:
      parkingSpotReservationsRepository.publish(cancelParkingSpotReservation(parkingSpotId, firstReservation))
    and:
      parkingSpotReservationsRepository.publish(cancelParkingSpotReservation(parkingSpotId, secondReservation))
    then:
      parkingSpotReservationsShouldBeFoundInDatabaseBeingFree(parkingSpotId)
  }
  
  private PartRequestOfParkingSpotReserved createReservationForPartOfParkingSpot(
      ParkingSpotId parkingSpotId, ReservationId reservationId, VehicleSize vehicleSize) {
    return new PartRequestOfParkingSpotReserved(parkingSpotId, reservationId, vehicleSize)
  }
  
  private ParkingSpotReservationRequestCancelled cancelParkingSpotReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    return new ParkingSpotReservationRequestCancelled(parkingSpotId, reservationId)
  }
  
  private void parkingSpotReservationsShouldBeFoundInDatabaseCannotHandlingMoreReservations(ParkingSpotId parkingSpotId) {
    def clientReservations = loadPersistedParkingSpotReservations(parkingSpotId)
    assert clientReservations.cannotHandleMore()
  }
  
  private void parkingSpotReservationsShouldBeFoundInDatabaseBeingFree(ParkingSpotId parkingSpotId) {
    def clientReservations = loadPersistedParkingSpotReservations(parkingSpotId)
    assert clientReservations.isFree()
  }
  
  ParkingSpotReservationRequests loadPersistedParkingSpotReservations(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotReservationRequests> loaded = parkingSpotReservationsRepository.findBy(parkingSpotId)
    ParkingSpotReservationRequests parkingSpotReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotReservations
  }
  
}
