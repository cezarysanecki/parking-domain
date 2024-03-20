package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastrcuture

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure.ClientsReservationsViewConfig
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationStatus
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationsView
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientsReservationsViews
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated
import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationStatus.Approved
import static pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationStatus.Cancelled
import static pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationStatus.Pending
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientsReservationsViewConfig.class, EventPublisherTestConfig.class])
class CreatingClientReservationViewTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ClientsReservationsViews clientsReservationsView
  
  def "reservation request should have proper status as process goes on"() {
    given:
      def reservationRequestCreated = reservationRequestCreated()
    and:
      def reservationId = reservationRequestCreated.reservationId
    
    when:
      eventPublisher.publish reservationRequestCreated
    then:
      reservationShouldBeInSpecificStatus(clientId, reservationId, Pending)
    
    when:
      eventPublisher.publish reservationMade(reservationId)
    
    then:
      reservationShouldBeInSpecificStatus(clientId, reservationId, Approved)
    
    when:
      eventPublisher.publish reservationCancelled(reservationId)
    
    then:
      reservationShouldBeInSpecificStatus(clientId, reservationId, Cancelled)
  }
  
  void reservationShouldBeInSpecificStatus(ClientId clientId, ReservationId reservationId, ClientReservationStatus status) {
    def clientReservationsView = loadPersistedClientReservationsView(clientId)
    assert clientReservationsView.reservations
        .any { it.reservationId == reservationId.value && it.status == status }
  }
  
  ReservationRequestCreated reservationRequestCreated() {
    return new ReservationRequestCreated(clientId, new ReservationSlot(LocalDateTime.now(), 3), Option.of(parkingSpotId))
  }
  
  ReservationMade reservationMade(ReservationId reservationId) {
    return new ReservationMade(parkingSpotId, clientId, reservationId, new ReservationSlot(LocalDateTime.now(), 3))
  }
  
  ReservationCancelled reservationCancelled(ReservationId reservationId) {
    return new ReservationCancelled(parkingSpotId, clientId, reservationId)
  }
  
  ClientReservationsView loadPersistedClientReservationsView(ClientId clientId) {
    return clientsReservationsView.findFor(clientId)
  }
  
}
