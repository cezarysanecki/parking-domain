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
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository

import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType.Bronze

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class HandlingClientReservationRequestIT extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ParkingSpotReservationsRepository reservationSchedules
  
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
  
  pl.cezarysanecki.parkingdomain.reservation.model.ReservationId shouldBePresentReservationForClient(ClientId clientId) {
    def reservationSchedule = loadPersistedReservationSchedule(parkingSpotId)
    assert reservationSchedule.thereIsReservationFor(clientId)
    return reservationSchedule.findReservationsFor(clientId).first()
  }
  
  void shouldBeNoReservationFor(ParkingSpotId parkingSpotId) {
    def reservationSchedule = loadPersistedReservationSchedule(parkingSpotId)
    assert reservationSchedule.isEmpty()
  }
  
  ParkingSpotCreated parkingSpotCreated() {
    return new ParkingSpotCreated(parkingSpotId, Bronze, 4)
  }
  
  ReservationRequestCreated reservationRequestCreated() {
    return new ReservationRequestCreated(clientId, new ReservationSlot(LocalDateTime.now(), 3), Option.none())
  }
  
  ParkingSpotReservationsEvent.ReservationCancelled reservationCancelled(pl.cezarysanecki.parkingdomain.reservation.model.ReservationId reservationId) {
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
