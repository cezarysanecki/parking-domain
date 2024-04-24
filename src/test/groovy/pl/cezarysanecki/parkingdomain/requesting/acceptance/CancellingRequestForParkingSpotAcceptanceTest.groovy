package pl.cezarysanecki.parkingdomain.requesting.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.client.application.CancellingRequest
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForWholeParkingSpot
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requesting.client.model.RequestId
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews

class CancellingRequestForParkingSpotAcceptanceTest extends AbstractRequestingAcceptanceTest {
  
  @Autowired
  MakingRequestForWholeParkingSpot makingRequestForWholeParkingSpot
  @Autowired
  MakingRequestForPartOfParkingSpot makingRequestForPartOfParkingSpot
  @Autowired
  CancellingRequest cancellingRequest
  
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
      cancellingRequest.cancelRequest(new CancellingRequest.Command(requestId))
    
    then:
      thereIsNoSuchRequestOn(parkingSpotId, requestId)
  }
  
  private RequestId makeRequestForWholeParkingSpot(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = makingRequestForWholeParkingSpot.makeRequest(new MakingRequestForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<RequestId>).getResult()
  }
  
  void thereIsNoSuchRequestOn(ParkingSpotId parkingSpotId, RequestId requestId) {
    assert parkingSpotRequestsViews.getAllParkingSpots()
        .find { it.parkingSpotId == parkingSpotId.value }
        .every {
          !it.currentRequests.contains(requestId.value)
        }
  }
  
}
