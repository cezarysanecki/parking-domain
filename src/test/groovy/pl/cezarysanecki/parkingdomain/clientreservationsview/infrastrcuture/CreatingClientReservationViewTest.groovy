package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastrcuture


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId
import pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure.ClientsReservationsViewConfig
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientReservationsView
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientsReservationsView
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleFixture.anyReservationId

@ActiveProfiles("local")
@SpringBootTest(classes = [ClientsReservationsViewConfig.class, EventPublisherTestConfig.class])
class CreatingClientReservationViewTest extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ClientsReservationsView clientsReservationsView
  
  def "reservation request is erased if reservation is cancelled"() {
    when:
      eventPublisher.publish reservationMade()
    
    then:
      reservationsShouldContainSpecificReservation(clientId, reservationId)
    
    when:
      eventPublisher.publish reservationCancelled()
    
    then:
      reservationsShouldNotContainSpecificReservation(clientId, reservationId)
  }
  
  void reservationsShouldContainSpecificReservation(ClientId clientId, ReservationId reservationId) {
    def clientReservationsView = loadPersistedClientReservationsView(clientId)
    assert clientReservationsView.reservations
        .any { it.reservationId == reservationId.value }
  }
  
  void reservationsShouldNotContainSpecificReservation(ClientId clientId, ReservationId reservationId) {
    def clientReservationsView = loadPersistedClientReservationsView(clientId)
    assert !clientReservationsView.reservations
        .any { it.reservationId == reservationId.value }
  }
  
  ReservationMade reservationMade() {
    return new ReservationMade(parkingSpotId, clientId, reservationId, new ReservationSlot(LocalDateTime.now(), 3))
  }
  
  ReservationCancelled reservationCancelled() {
    return new ReservationCancelled(parkingSpotId, clientId, reservationId)
  }
  
  ClientReservationsView loadPersistedClientReservationsView(ClientId clientId) {
    return clientsReservationsView.findFor(clientId)
  }
  
}
