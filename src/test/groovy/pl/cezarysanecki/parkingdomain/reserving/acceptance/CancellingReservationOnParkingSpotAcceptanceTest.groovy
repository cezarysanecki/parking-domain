package pl.cezarysanecki.parkingdomain.reserving.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reserving.client.application.CancellingReservationRequest
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingWholeParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.view.parkingspot.model.ParkingSpotReservationsViews

class CancellingReservationOnParkingSpotAcceptanceTest extends AbstractReservingAcceptanceTest {
  
  @Autowired
  ReservingWholeParkingSpot reservingWholeParkingSpot
  @Autowired
  ReservingPartOfParkingSpot reservingPartOfParkingSpot
  @Autowired
  CancellingReservationRequest cancellingReservationRequest
  
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
