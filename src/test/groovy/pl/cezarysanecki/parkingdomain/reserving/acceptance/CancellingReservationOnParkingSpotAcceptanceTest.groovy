package pl.cezarysanecki.parkingdomain.reserving.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reserving.ReservingConfig
import pl.cezarysanecki.parkingdomain.reserving.client.application.CancellingReservationRequest
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingWholeParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.view.parkingspot.model.ParkingSpotReservationsViews
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [ReservingConfig.class, EventPublisherTestConfig.class])
class CancellingReservationOnParkingSpotAcceptanceTest extends Specification {
  
  @Autowired
  ReservingWholeParkingSpot reservingWholeParkingSpot
  @Autowired
  ReservingPartOfParkingSpot reservingPartOfParkingSpot
  @Autowired
  CancellingReservationRequest cancellingReservationRequest
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ParkingSpotReservationsViews parkingSpotReservationsViews
  
  def "cancel reservation on parking spot"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
    and:
      def reservationId = reserveWholeParkingSpotFor(clientId, parkingSpotId)
    
    when:
      cancellingReservationRequest.cancelReservationRequest(new CancellingReservationRequest.Command(reservationId))
    
    then:
      thereIsNoSuchReservationOn(parkingSpotId, reservationId)
  }
  
  private ParkingSpotId createParkingSpot(int capacity) {
    def parkingSpotId = ParkingSpotId.newOne()
    eventPublisher.publish(new CreatingParkingSpot.ParkingSpotCreated(
        parkingSpotId, ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold))
    return parkingSpotId
  }
  
  private ReservationId reserveWholeParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = reservingWholeParkingSpot.requestReservation(new ReservingWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  void thereIsNoSuchReservationOn(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .find { it.parkingSpotId == parkingSpotId.value }
        .every {
          !it.currentReservations.contains(reservationId.value)
        }
  }
  
}
