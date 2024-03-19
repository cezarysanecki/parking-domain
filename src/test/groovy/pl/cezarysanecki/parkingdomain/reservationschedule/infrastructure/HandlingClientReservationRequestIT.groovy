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

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled

@ActiveProfiles("local")
@SpringBootTest(classes = [ReservationScheduleConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class HandlingClientReservationRequestIT extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ReservationSchedules reservationSchedules
  
  def 'persistence in database should work'() {
    given:
      eventPublisher.publish parkingSpotCreated()
    
    when:
      eventPublisher.publish reservationRequestCreated()
    then:
      def reservationId = shouldBePresentReservationForClient(clientId)
    
    when:
      reservationSchedules.publish reservationCancelled(reservationId)
    then:
      shouldBeNoReservationFor(parkingSpotId)
  }
  
  ReservationId shouldBePresentReservationForClient(ClientId clientId) {
    def reservationSchedule = loadPersistedReservationSchedule(parkingSpotId)
    assert reservationSchedule.thereIsReservationFor(clientId)
    return reservationSchedule.findReservationsFor(clientId).first()
  }
  
  void shouldBeNoReservationFor(ParkingSpotId parkingSpotId) {
    def reservationSchedule = loadPersistedReservationSchedule(parkingSpotId)
    assert reservationSchedule.isEmpty()
  }
  
  ParkingSpotCreated parkingSpotCreated() {
    return new ParkingSpotCreated(parkingSpotId, 4)
  }
  
  ReservationRequestCreated reservationRequestCreated() {
    return new ReservationRequestCreated(clientId, new ReservationSlot(LocalDateTime.now(), 3), Option.none())
  }
  
  ReservationCancelled reservationCancelled(ReservationId reservationId) {
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
