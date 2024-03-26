package pl.cezarysanecki.parkingdomain.reservation.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType.Bronze
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class ReservationScheduleInMemoryRepositoryIT extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ClientId clientId = anyClientId()
  pl.cezarysanecki.parkingdomain.reservation.model.ReservationId reservationId = anyReservationId()
  
  @Autowired
  EventPublisher eventPublisher
  
  @Autowired
  pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository reservationSchedules
  
  def 'persistence in database should work'() {
    given:
      eventPublisher.publish(new ParkingSpotCreated(parkingSpotId, Bronze, 4))
    
    when:
      reservationSchedules.publish reservationMade(
          new ReservationSlot(LocalDateTime.of(2024, 10, 10, 10, 0), 4))
    
    then:
      reservationScheduleShouldHaveReservationForClient(parkingSpotId, clientId)
    
    and:
      reservationSchedules.publish reservationCancelled()
    
    then:
      reservationScheduleShouldBeEmpty(parkingSpotId)
  }
  
  void reservationScheduleShouldHaveReservationForClient(ParkingSpotId parkingSpotId, ClientId clientId) {
    def reservationSchedule = loadPersistedReservationSchedule(parkingSpotId)
    assert reservationSchedule.thereIsReservationFor(clientId)
  }
  
  void reservationScheduleShouldBeEmpty(ParkingSpotId parkingSpotId) {
    def reservationSchedule = loadPersistedReservationSchedule(parkingSpotId)
    assert reservationSchedule.isEmpty()
  }
  
  ReservationMade reservationMade(ReservationSlot reservationSlot) {
    return new ReservationMade(parkingSpotId, clientId, reservationId, reservationSlot)
  }
  
  ParkingSpotReservationsEvent.ReservationCancelled reservationCancelled() {
    return new ParkingSpotReservationsEvent.ReservationCancelled(parkingSpotId, clientId, reservationId)
  }
  
  ReservationSchedule loadPersistedReservationSchedule(ParkingSpotId parkingSpotId) {
    Option<ReservationSchedule> loaded = reservationSchedules.findBy(parkingSpotId)
    ReservationSchedule reservationSchedule = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return reservationSchedule
  }
}
