package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyReservationId

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class MakingReservationRequestIT extends Specification {
  
  ClientId clientId = anyClientId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ClientReservationsRepository clientReservationsRepository
  
  def "reservation request is erased if reservation is cancelled"() {
    when:
      clientReservationsRepository.publish reservationRequestCreated()
    
    then:
      clientReservationsShouldNotBeEmpty(clientId)
    
    when:
      eventPublisher.publish reservationCancelled()
    
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
  
  ReservationCancelled reservationCancelled() {
    return new ReservationCancelled(anyParkingSpotId(), clientId, anyReservationId())
  }
  
  ClientReservations loadPersistedClientReservations(ClientId clientId) {
    Option<ClientReservations> loaded = clientReservationsRepository.findBy(clientId)
    ClientReservations clientReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservations
  }
  
}
