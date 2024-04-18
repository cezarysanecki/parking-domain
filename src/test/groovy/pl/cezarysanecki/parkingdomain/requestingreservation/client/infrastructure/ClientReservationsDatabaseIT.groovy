package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture.ClientReservationsConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservations
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsEvent.ReservationRequestCancelled

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationsConfig.class])
class ClientReservationsDatabaseIT extends Specification {
  
  def clientId = ClientId.newOne()
  def reservationId = ReservationId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ClientReservationsRepository clientReservationsRepository
  
  def "persistence of client reservations in real database should work"() {
    when:
      clientReservationsRepository.publish(createClientReservationRequest(clientId, reservationId))
    then:
      clientReservationsShouldBeFoundInDatabaseWithReservation(clientId, reservationId)
    
    when:
      clientReservationsRepository.publish(cancelClientReservationRequest(clientId, reservationId))
    then:
      clientReservationsShouldBeFoundInDatabaseBeingEmpty(clientId)
  }
  
  private ReservationForWholeParkingSpotSubmitted createClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new ReservationForWholeParkingSpotSubmitted(clientId, reservationId, ParkingSpotId.newOne())
  }
  
  private ReservationRequestCancelled cancelClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new ReservationRequestCancelled(clientId, reservationId)
  }
  
  private void clientReservationsShouldBeFoundInDatabaseWithReservation(ClientId clientId, ReservationId reservationId) {
    def clientReservations = loadPersistedClientReservations(clientId)
    assert clientReservations.contains(reservationId)
  }
  
  private void clientReservationsShouldBeFoundInDatabaseBeingEmpty(ClientId clientId) {
    def clientReservations = loadPersistedClientReservations(clientId)
    assert clientReservations.isEmpty()
  }
  
  ClientReservations loadPersistedClientReservations(ClientId clientId) {
    Option<ClientReservations> loaded = clientReservationsRepository.findBy(clientId)
    ClientReservations clientReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservations
  }
  
}
