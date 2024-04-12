package pl.cezarysanecki.parkingdomain.reserving.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.infrastucture.ParkingSpotReservationsConfig
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservations
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsRepository
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotReservationsConfig.class])
class ParkingSpotReservationsDatabaseIT extends Specification {
  
  def parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpotReservationsRepository parkingSpotReservationsRepository
  
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
  
  private PartOfParkingSpotReserved createReservationForPartOfParkingSpot(
      ParkingSpotId parkingSpotId, ReservationId reservationId, VehicleSize vehicleSize) {
    return new PartOfParkingSpotReserved(parkingSpotId, reservationId, vehicleSize)
  }
  
  private ParkingSpotReservationCancelled cancelParkingSpotReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    return new ParkingSpotReservationCancelled(parkingSpotId, reservationId)
  }
  
  private void parkingSpotReservationsShouldBeFoundInDatabaseCannotHandlingMoreReservations(ParkingSpotId parkingSpotId) {
    def clientReservations = loadPersistedParkingSpotReservations(parkingSpotId)
    assert clientReservations.cannotHandleMore()
  }
  
  private void parkingSpotReservationsShouldBeFoundInDatabaseBeingFree(ParkingSpotId parkingSpotId) {
    def clientReservations = loadPersistedParkingSpotReservations(parkingSpotId)
    assert clientReservations.isFree()
  }
  
  ParkingSpotReservations loadPersistedParkingSpotReservations(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotReservations> loaded = parkingSpotReservationsRepository.findBy(parkingSpotId)
    ParkingSpotReservations parkingSpotReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotReservations
  }
  
}
