package pl.cezarysanecki.parkingdomain.reservation.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationCancelled
import pl.cezarysanecki.parkingdomain.reservation.model.events.ReservationForWholeParkingSpotMade
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyReservationId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType.Bronze

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class ParkingSpotReservationsInMemoryRepositoryIT extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  @Autowired
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpotReservationsRepository parkingSpotReservationsRepository
  
  def 'persistence in database should work'() {
    given:
      eventPublisher.publish(new ParkingSpotCreated(parkingSpotId, Bronze, 4))
    
    when:
      parkingSpotReservationsRepository.publish reservationForWholeParkingSpotMade()
    
    then:
      reservationScheduleShouldHaveReservationForClient(parkingSpotId, reservationId)
    
    and:
      parkingSpotReservationsRepository.publish reservationCancelled()
    
    then:
      reservationScheduleShouldBeEmpty(parkingSpotId)
  }
  
  void reservationScheduleShouldHaveReservationForClient(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    def reservationSchedule = loadPersistedParkingSpotReservations(parkingSpotId)
    assert reservationSchedule.contains(reservationId)
  }
  
  void reservationScheduleShouldBeEmpty(ParkingSpotId parkingSpotId) {
    def reservationSchedule = loadPersistedParkingSpotReservations(parkingSpotId)
    assert reservationSchedule.isEmpty()
  }
  
  ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade() {
    return new ReservationForWholeParkingSpotMade(reservationId, ReservationPeriod.wholeDay(), parkingSpotId)
  }
  
  ReservationCancelled reservationCancelled() {
    return new ReservationCancelled(parkingSpotId, reservationId)
  }
  
  ParkingSpotReservations loadPersistedParkingSpotReservations(ParkingSpotId parkingSpotId) {
    Option<ParkingSpotReservations> loaded = parkingSpotReservationsRepository.findBy(parkingSpotId)
    ParkingSpotReservations parkingSpotReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpotReservations
  }
}
