package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class ClientReservationsInMemoryRepositoryIT extends Specification {
  
  ClientId clientId = anyClientId()
  
  @Autowired
  ClientReservationsRepository clientReservationsRepository
  
  def 'persistence in database should work'() {
    when:
      clientReservationsRepository.publish reservationRequestCreated()
    
    then:
      clientReservationsShouldNotBeEmpty(clientId)
    
    when:
      clientReservationsRepository.publish reservationRequestCancelled()
    
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
  
  ReservationRequestCancelled reservationRequestCancelled() {
    return new ReservationRequestCancelled(clientId)
  }
  
  ClientReservations loadPersistedClientReservations(ClientId clientId) {
    Option<ClientReservations> loaded = clientReservationsRepository.findBy(clientId)
    ClientReservations clientReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservations
  }
  
}
