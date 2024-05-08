package pl.cezarysanecki.parkingdomain.acceptance.requestingreservation

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CancellingReservationRequest

import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId
import pl.cezarysanecki.parkingdomain.requestingreservation.web.parkingspot.model.ParkingSpotRequestsViews

class CancellingReservationRequestForParkingSpotAcceptanceTest extends AbstractRequestingAcceptanceTest {
  
  @Autowired
  MakingRequestForWholeParkingSpot makingRequestForWholeParkingSpot
  @Autowired
  MakingRequestForPartOfParkingSpot makingRequestForPartOfParkingSpot
  @Autowired
  CancellingReservationRequest cancellingRequest
  
  @Autowired
  ParkingSpotRequestsViews parkingSpotRequestsViews
  
  def "cancel parking spot request"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
    and:
      def requestId = makeRequestForWholeParkingSpot(clientId, parkingSpotId)
    
    when:
      cancellingRequest.cancelRequest(new CancellingReservationRequest.Command(requestId))
    
    then:
      thereIsNoSuchRequestOn(parkingSpotId, requestId)
  }
  
  private ReservationRequestId makeRequestForWholeParkingSpot(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = makingRequestForWholeParkingSpot.makeRequest(new MakingRequestForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationRequestId>).getResult()
  }
  
  void thereIsNoSuchRequestOn(ParkingSpotId parkingSpotId, ReservationRequestId requestId) {
    assert parkingSpotRequestsViews.getAllParkingSpots()
        .find { it.parkingSpotId == parkingSpotId.value }
        .every {
          !it.currentRequests.contains(requestId.value)
        }
  }
  
}
