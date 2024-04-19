package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture.ClientReservationRequestsConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForWholeParkingSpotRequested
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationRequestsConfig.class])
class ClientReservationRequestsDatabaseIT extends Specification {
  
  def clientId = ClientId.newOne()
  def reservationId = ReservationId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ClientReservationRequestsRepository clientReservationsRepository
  
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
  
  private ReservationForWholeParkingSpotRequested createClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new ReservationForWholeParkingSpotRequested(clientId, reservationId, ParkingSpotId.newOne())
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
  
  ClientReservationRequests loadPersistedClientReservations(ClientId clientId) {
    Option<ClientReservationRequests> loaded = clientReservationsRepository.findBy(clientId)
    ClientReservationRequests clientReservations = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservations
  }
  
}
