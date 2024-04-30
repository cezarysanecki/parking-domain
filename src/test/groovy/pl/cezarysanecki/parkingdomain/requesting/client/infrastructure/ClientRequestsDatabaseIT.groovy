package pl.cezarysanecki.parkingdomain.requesting.client.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.infrastucture.ClientRequestsConfig
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequests
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientRequestsConfig.class])
class ClientRequestsDatabaseIT extends Specification {
  
  def clientId = ClientId.newOne()
  def requestId = RequestId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ClientRequestsRepository clientRequestsRepository
  
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
  
  private static RequestForWholeParkingSpotMade createClientRequest(ClientId clientId, RequestId requestId) {
    return new RequestForWholeParkingSpotMade(clientId, requestId, ParkingSpotId.newOne())
  }
  
  private static RequestCancelled cancelClientRequest(ClientId clientId, RequestId requestId) {
    return new RequestCancelled(clientId, requestId)
  }
  
  private void clientRequestsShouldBeFoundInDatabaseWithRequest(ClientId clientId, RequestId requestId) {
    def clientRequests = loadPersistedClientRequests(clientId)
    assert clientRequests.contains(requestId)
  }
  
  private void clientRequestsShouldBeFoundInDatabaseBeingEmpty(ClientId clientId) {
    def clientRequests = loadPersistedClientRequests(clientId)
    assert clientRequests.isEmpty()
  }
  
  ClientRequests loadPersistedClientRequests(ClientId clientId) {
    Option<ClientRequests> loaded = clientRequestsRepository.findBy(clientId)
    ClientRequests clientRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientRequests
  }
  
}
