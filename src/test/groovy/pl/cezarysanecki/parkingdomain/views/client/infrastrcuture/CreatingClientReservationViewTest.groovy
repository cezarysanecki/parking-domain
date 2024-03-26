package pl.cezarysanecki.parkingdomain.views.client.infrastrcuture

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod
import pl.cezarysanecki.parkingdomain.views.client.infrastructure.ClientsReservationsViewConfig
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationStatus
import pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationsView
import pl.cezarysanecki.parkingdomain.views.client.model.ClientsReservationsViews
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent.ReservationForWholeParkingSpotMade
import static pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationStatus.Approved
import static pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationStatus.Cancelled
import static pl.cezarysanecki.parkingdomain.views.client.model.ClientReservationStatus.Pending

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientsReservationsViewConfig.class, EventPublisherTestConfig.class])
class CreatingClientReservationViewTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationPeriod reservationPeriod = ReservationPeriod.evening()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ClientsReservationsViews clientsReservationsView
  
  def "reservation request should have proper status as process goes on"() {
    given:
      def chosenParkingSpotReservationRequested = chosenParkingSpotReservationRequested()
    and:
      def reservationId = chosenParkingSpotReservationRequested.reservationId
    
    when:
      eventPublisher.publish chosenParkingSpotReservationRequested
    then:
      reservationShouldBeInSpecificStatus(clientId, reservationId, Pending)
    
    when:
      eventPublisher.publish reservationForWholeParkingSpotMade(reservationId)
    
    then:
      reservationShouldBeInSpecificStatus(clientId, reservationId, Approved)
    
    when:
      eventPublisher.publish reservationRequestCancelled(reservationId)
    
    then:
      reservationShouldBeInSpecificStatus(clientId, reservationId, Cancelled)
  }
  
  void reservationShouldBeInSpecificStatus(ClientId clientId, ReservationId reservationId, ClientReservationStatus status) {
    def clientReservationsView = loadPersistedClientReservationsView(clientId)
    assert clientReservationsView.reservations
        .any { it.reservationId == reservationId.value && it.status == status }
  }
  
  ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested() {
    return new ChosenParkingSpotReservationRequested(clientId, reservationPeriod, parkingSpotId)
  }
  
  ReservationForWholeParkingSpotMade reservationForWholeParkingSpotMade(ReservationId reservationId) {
    return new ReservationForWholeParkingSpotMade(reservationId, reservationPeriod, parkingSpotId)
  }
  
  ReservationRequestCancelled reservationRequestCancelled(ReservationId reservationId) {
    return new ReservationRequestCancelled(clientId, reservationId)
  }
  
  ClientReservationsView loadPersistedClientReservationsView(ClientId clientId) {
    return clientsReservationsView.findFor(clientId)
  }
  
}
