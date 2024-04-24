package pl.cezarysanecki.parkingdomain.requesting.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.application.CancellingRequest
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForWholeParkingSpot
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews

class CancellingRequestForParkingSpotAcceptanceTest extends AbstractRequestingReservationsAcceptanceTest {
  
  @Autowired
  MakingRequestForWholeParkingSpot reservingWholeParkingSpot
  @Autowired
  MakingRequestForPartOfParkingSpot reservingPartOfParkingSpot
  @Autowired
  CancellingRequest cancellingReservationRequest
  
  @Autowired
  ParkingSpotRequestsViews parkingSpotReservationsViews
  
  def "cancel reservation on parking spot"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
    and:
      def reservationId = reserveWholeParkingSpotFor(clientId, parkingSpotId)
    
    when:
      cancellingReservationRequest.cancelRequest(new CancellingRequest.Command(reservationId))
    
    then:
      thereIsNoSuchReservationOn(parkingSpotId, reservationId)
  }
  
  private ReservationId reserveWholeParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = reservingWholeParkingSpot.makeRequest(new MakingRequestForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  void thereIsNoSuchReservationOn(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .find { it.parkingSpotId == parkingSpotId.value }
        .every {
          !it.currentRequests.contains(reservationId.value)
        }
  }
  
}
