package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequests
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ChosenParkingSpotReservationRequested
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.events.ReservationRequestCancelled
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientReservationsConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class ClientReservationRequestsInMemoryRepositoryIT extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  @Autowired
  ClientReservationRequestsRepository clientReservationRequestsRepository
  
  def 'persistence in database should work'() {
    given:
      def chosenParkingSpotReservationRequested = chosenParkingSpotReservationRequested()
    
    when:
      clientReservationRequestsRepository.publish chosenParkingSpotReservationRequested
    then:
      clientReservationRequestsShouldNotBeEmpty(clientId)
    
    when:
      clientReservationRequestsRepository.publish reservationRequestCancelled(chosenParkingSpotReservationRequested.reservationId)
    then:
      clientReservationRequestsShouldBeEmpty(clientId)
  }
  
  void clientReservationRequestsShouldNotBeEmpty(ClientId clientId) {
    def clientReservationRequests = loadPersistedClientReservationRequests(clientId)
    assert !clientReservationRequests.isEmpty()
  }
  
  void clientReservationRequestsShouldBeEmpty(ClientId clientId) {
    def clientReservationRequests = loadPersistedClientReservationRequests(clientId)
    assert clientReservationRequests.isEmpty()
  }
  
  ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested() {
    return new ChosenParkingSpotReservationRequested(clientId, ReservationPeriod.wholeDay(), parkingSpotId)
  }
  
  ReservationRequestCancelled reservationRequestCancelled(ReservationId reservationId) {
    return new ReservationRequestCancelled(clientId, reservationId)
  }
  
  ClientReservationRequests loadPersistedClientReservationRequests(ClientId clientId) {
    Option<ClientReservationRequests> loaded = clientReservationRequestsRepository.findBy(clientId)
    def clientReservationRequests = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return clientReservationRequests
  }
  
}
