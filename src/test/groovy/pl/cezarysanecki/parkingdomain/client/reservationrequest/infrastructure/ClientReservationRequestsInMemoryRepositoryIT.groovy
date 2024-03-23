package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsFixture.anyClientId

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class ClientReservationRequestsInMemoryRepositoryIT extends Specification {
  
  ClientId clientId = anyClientId()
  
  @Autowired
  ClientReservationRequestsRepository clientReservationsRepository
  
  def 'persistence in database should work'() {
    given:
      def reservationRequestCreated = reservationRequestCreated()
    
    when:
      clientReservationsRepository.publish reservationRequestCreated
    then:
      clientReservationsShouldNotBeEmpty(clientId)
    
    when:
      clientReservationsRepository.publish reservationRequestCancelled(reservationRequestCreated.reservationId)
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
  
  ReservationRequestCancelled reservationRequestCancelled(ReservationId reservationId) {
    return new ReservationRequestCancelled(clientId, reservationId)
  }
  
  ClientReservationRequests loadPersistedClientReservations(ClientId clientId) {
    return clientReservationsRepository.findBy(clientId)
  }
  
}
