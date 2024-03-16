package pl.cezarysanecki.parkingdomain.reservationschedule.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyReservationId

@ActiveProfiles("local")
@SpringBootTest(classes = [ReservationScheduleConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class ReservationScheduleInMemoryRepositoryIT extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ClientId clientId = anyClientId()
  ReservationId reservationId = anyReservationId()
  
  @Autowired
  EventPublisher eventPublisher
  
  @Autowired
  ReservationSchedules reservationSchedules
  
  def 'persistence in database should work'() {
    given:
      eventPublisher.publish(new ParkingSpotCreated(parkingSpotId, 4))
    
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
  
  ReservationCancelled reservationCancelled() {
    return new ReservationCancelled(parkingSpotId, clientId, reservationId)
  }
  
  ReservationSchedule loadPersistedReservationSchedule(ParkingSpotId parkingSpotId) {
    Option<ReservationSchedule> loaded = reservationSchedules.findBy(parkingSpotId)
    ReservationSchedule reservationSchedule = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return reservationSchedule
  }
}
