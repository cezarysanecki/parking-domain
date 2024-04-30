package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleSize
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForWholeParkingSpot
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews
import spock.lang.Ignore

@Ignore("#256")
class AllowingToRequestReservationOnParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  MakingRequestForWholeParkingSpot requestingReservationForWholeParkingSpot
  @Autowired
  MakingRequestForPartOfParkingSpot requestingReservationForPartOfParkingSpot
  
  @Autowired
  ParkingSpotRequestsViews parkingSpotReservationsViews
  
  def "reserve whole parking spot"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
    
    when:
      def reservationId = reserveWholeParkingSpotFor(clientId, parkingSpotId)
    
    then:
      thereIsReservation(parkingSpotId, reservationId)
  }
  
  def "reserve part of parking spot"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
    
    when:
      def reservationId = reservePartOfParkingSpotFor(clientId, parkingSpotId, 2)
    
    then:
      thereIsReservation(parkingSpotId, reservationId)
  }
  
  private ReservationId reserveWholeParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = requestingReservationForWholeParkingSpot.makeRequest(new MakingRequestForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  private ReservationId reservePartOfParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId, int vehicleSize) {
    def result = requestingReservationForPartOfParkingSpot.makeRequest(new MakingRequestForPartOfParkingSpot.Command(
        clientId, parkingSpotId, VehicleSize.of(vehicleSize)))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  void thereIsReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .any {
          it.parkingSpotId == parkingSpotId.value
              && it.currentRequests.contains(reservationId.value)
        }
  }
  
}
