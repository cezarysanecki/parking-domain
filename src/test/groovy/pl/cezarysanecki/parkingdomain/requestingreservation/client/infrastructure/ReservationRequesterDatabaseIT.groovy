package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId

import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ClientRequestsEvent.RequestCancelled
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ClientRequestsEvent.RequestForWholeParkingSpotMade

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientRequestsConfig.class])
class ReservationRequesterDatabaseIT extends Specification {
  
  def clientId = ClientId.newOne()
  def requestId = ReservationRequestId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ReservationRequesterRepository clientRequestsRepository
  
  def "persistence of client requests in real database should work"() {
    when:
      clientRequestsRepository.publish(createClientRequest(clientId, requestId))
    then:
      clientRequestsShouldBeFoundInDatabaseWithRequest(clientId, requestId)
    
    when:
      clientRequestsRepository.publish(cancelClientRequest(clientId, requestId))
    then:
      clientRequestsShouldBeFoundInDatabaseBeingEmpty(clientId)
  }
  
  private static RequestForWholeParkingSpotMade createClientRequest(ClientId clientId, ReservationRequestId requestId) {
    return new RequestForWholeParkingSpotMade(clientId, requestId, ParkingSpotId.newOne())
  }
  
  private static RequestCancelled cancelClientRequest(ClientId clientId, ReservationRequestId requestId) {
    return new RequestCancelled(clientId, requestId)
  }
  
  private void clientRequestsShouldBeFoundInDatabaseWithRequest(ClientId clientId, ReservationRequestId requestId) {
    def clientRequests = loadPersistedClientRequests(clientId)
    assert clientRequests.contains(requestId)
  }
  
  private void clientRequestsShouldBeFoundInDatabaseBeingEmpty(ClientId clientId) {
    def clientRequests = loadPersistedClientRequests(clientId)
    assert clientRequests.isEmpty()
  }
  
  ReservationRequester loadPersistedClientRequests(ClientId clientId) {
    Option<ReservationRequester> loaded = clientRequestsRepository.findBy(clientId)
    ReservationRequester clientRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientRequests
  }
  
}
