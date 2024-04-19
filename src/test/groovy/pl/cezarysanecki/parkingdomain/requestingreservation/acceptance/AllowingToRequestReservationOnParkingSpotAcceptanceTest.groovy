package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForWholeParkingSpot
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationRequestsViews

class AllowingToRequestReservationOnParkingSpotAcceptanceTest extends AbstractRequestingReservationsAcceptanceTest {
  
  @Autowired
  RequestingReservationForWholeParkingSpot requestingReservationForWholeParkingSpot
  @Autowired
  RequestingReservationForPartOfParkingSpot requestingReservationForPartOfParkingSpot
  
  @Autowired
  ParkingSpotReservationRequestsViews parkingSpotReservationsViews
  
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
    def result = requestingReservationForWholeParkingSpot.requestReservation(new RequestingReservationForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  private ReservationId reservePartOfParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId, int vehicleSize) {
    def result = requestingReservationForPartOfParkingSpot.requestReservation(new RequestingReservationForPartOfParkingSpot.Command(
        clientId, parkingSpotId, VehicleSize.of(vehicleSize)))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  void thereIsReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .any {
          it.parkingSpotId == parkingSpotId.value
              && it.currentReservationRequests.contains(reservationId.value)
        }
  }
  
}
