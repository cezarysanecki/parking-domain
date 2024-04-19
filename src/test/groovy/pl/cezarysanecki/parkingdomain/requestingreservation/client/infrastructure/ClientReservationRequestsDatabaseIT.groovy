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
  ClientReservationRequestsRepository clientReservationRequestsRepository
  
  def "persistence of client reservation requests in real database should work"() {
    when:
      clientReservationRequestsRepository.publish(createClientReservationRequest(clientId, reservationId))
    then:
      clientReservationRequestsShouldBeFoundInDatabaseWithReservationRequest(clientId, reservationId)
    
    when:
      clientReservationRequestsRepository.publish(cancelClientReservationRequest(clientId, reservationId))
    then:
      clientReservationRequestsShouldBeFoundInDatabaseBeingEmpty(clientId)
  }
  
  private ReservationForWholeParkingSpotRequested createClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new ReservationForWholeParkingSpotRequested(clientId, reservationId, ParkingSpotId.newOne())
  }
  
  private ReservationRequestCancelled cancelClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new ReservationRequestCancelled(clientId, reservationId)
  }
  
  private void clientReservationRequestsShouldBeFoundInDatabaseWithReservationRequest(ClientId clientId, ReservationId reservationId) {
    def clientReservations = loadPersistedClientReservationRequests(clientId)
    assert clientReservations.contains(reservationId)
  }
  
  private void clientReservationRequestsShouldBeFoundInDatabaseBeingEmpty(ClientId clientId) {
    def clientReservations = loadPersistedClientReservationRequests(clientId)
    assert clientReservations.isEmpty()
  }
  
  ClientReservationRequests loadPersistedClientReservationRequests(ClientId clientId) {
    Option<ClientReservationRequests> loaded = clientReservationRequestsRepository.findBy(clientId)
    ClientReservationRequests clientReservationRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservationRequests
  }
  
}
