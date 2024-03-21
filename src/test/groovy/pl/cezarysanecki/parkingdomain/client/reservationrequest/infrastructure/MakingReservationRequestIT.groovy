package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservations
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleEvent.ReservationCancelled

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class MakingReservationRequestIT extends Specification {
  
  ClientId clientId = anyClientId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ClientReservationsRepository clientReservationsRepository
  
  def "reservation request is erased if reservation is cancelled"() {
    given:
      def reservationRequestCreated = reservationRequestCreated()
    
    when:
      clientReservationsRepository.publish reservationRequestCreated
    then:
      clientReservationsShouldNotBeEmpty(clientId)
    
    when:
      eventPublisher.publish reservationCancelled(reservationRequestCreated.reservationId)
    then:
      clientReservationsShouldBeEmpty(clientId)
  }
  
  void clientReservationsShouldNotBeEmpty(ClientId clientId) {
    def clientReservations = loadPersistedClientReservations(clientId)
    assert !clientReservations.isEmpty()
  }
  
  void clientReservationsShouldBeEmpty(ClientId clientId) {
    def clientReservations = loadPersistedClientReservations(clientId)
    assert clientReservations.isEmpty()
  }
  
  ReservationRequestCreated reservationRequestCreated() {
    return new ReservationRequestCreated(clientId, new ReservationSlot(LocalDateTime.now(), 3), Option.none())
  }
  
  ReservationCancelled reservationCancelled(ReservationId reservationId) {
    return new ReservationCancelled(anyParkingSpotId(), clientId, reservationId)
  }
  
  ClientReservations loadPersistedClientReservations(ClientId clientId) {
    return clientReservationsRepository.findBy(clientId)
  }
  
}
