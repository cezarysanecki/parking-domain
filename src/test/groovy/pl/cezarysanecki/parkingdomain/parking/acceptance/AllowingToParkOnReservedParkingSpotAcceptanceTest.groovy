package pl.cezarysanecki.parkingdomain.parking.acceptance

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForWholeParkingSpot
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews
import spock.lang.Ignore

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle.ParkOnReservedCommand

@Ignore("#256")
class AllowingToParkOnReservedParkingSpotAcceptanceTest extends AbstractParkingAcceptanceTest {
  
  @Autowired
  MakingRequestForWholeParkingSpot requestingReservationForWholeParkingSpot
  @Autowired
  ParkingVehicle parkingVehicle
  
  @Autowired
  VehicleViews vehicleViews
  @Autowired
  ParkingSpotRequestsViews parkingSpotRequestsViews
  
  def "allow to park on reserved parking spot"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
      def vehicleId = registerVehicle(2)
    and:
      def reservationId = reserveWholeParkingSpotFor(clientId, parkingSpotId)
    
    when:
      parkingVehicle.park(new ParkOnReservedCommand(vehicleId, reservationId))
    
    then:
      thereIsReservation(parkingSpotId, reservationId)
    and:
      vehicleIsParkedOn(parkingSpotId, vehicleId)
  }
  
  def "reject to park on reserved parking spot if this is not reserved for this client"() {
    given:
      ClientId clientId = ClientId.newOne()
    and:
      def parkingSpotId = createParkingSpot(4)
      def vehicleId = registerVehicle(2)
    and:
      def reservationId = reserveWholeParkingSpotFor(clientId, parkingSpotId)
    
    when:
      parkingVehicle.park(new ParkOnReservedCommand(vehicleId, ReservationId.newOne()))
    
    then:
      thereIsReservation(parkingSpotId, reservationId)
    and:
      vehicleIsNotParkedOn(parkingSpotId, vehicleId)
  }
  
  private ReservationId reserveWholeParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = requestingReservationForWholeParkingSpot.makeRequest(new MakingRequestForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  private void thereIsReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotRequestsViews.getAllParkingSpots()
        .any {
          it.parkingSpotId == parkingSpotId.value
              && it.currentRequests.contains(reservationId.value)
        }
  }
  
  private void vehicleIsParkedOn(ParkingSpotId parkingSpotId, VehicleId vehicleId) {
    def parkedVehicles = vehicleViews.queryForParkedVehicles()
        .stream()
        .filter { ParkingSpotId.of(it.parkingSpotId) == parkingSpotId }
        .toList()
    assert parkedVehicles.any { it.vehicleId == vehicleId.value }
  }
  
  private void vehicleIsNotParkedOn(ParkingSpotId parkingSpotId, VehicleId vehicleId) {
    def parkedVehicles = vehicleViews.queryForParkedVehicles()
        .stream()
        .filter { ParkingSpotId.of(it.parkingSpotId) == parkingSpotId }
        .toList()
    assert parkedVehicles.every() { it.vehicleId != vehicleId.value }
  }
  
}
