package pl.cezarysanecki.parkingdomain.requesting.client.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.infrastucture.ClientRequestsConfig
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequests
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientRequestsConfig.class])
class ClientRequestsDatabaseIT extends Specification {
  
  def clientId = ClientId.newOne()
  def reservationId = ReservationId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ClientRequestsRepository clientReservationRequestsRepository
  
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
  
  private RequestForWholeParkingSpotMade createClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new RequestForWholeParkingSpotMade(clientId, reservationId, ParkingSpotId.newOne())
  }
  
  private RequestCancelled cancelClientReservationRequest(ClientId clientId, ReservationId reservationId) {
    return new RequestCancelled(clientId, reservationId)
  }
  
  private void clientReservationRequestsShouldBeFoundInDatabaseWithReservationRequest(ClientId clientId, ReservationId reservationId) {
    def clientReservations = loadPersistedClientReservationRequests(clientId)
    assert clientReservations.contains(reservationId)
  }
  
  private void clientReservationRequestsShouldBeFoundInDatabaseBeingEmpty(ClientId clientId) {
    def clientReservations = loadPersistedClientReservationRequests(clientId)
    assert clientReservations.isEmpty()
  }
  
  ClientRequests loadPersistedClientReservationRequests(ClientId clientId) {
    Option<ClientRequests> loaded = clientReservationRequestsRepository.findBy(clientId)
    ClientRequests clientReservationRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservationRequests
  }
  
}
