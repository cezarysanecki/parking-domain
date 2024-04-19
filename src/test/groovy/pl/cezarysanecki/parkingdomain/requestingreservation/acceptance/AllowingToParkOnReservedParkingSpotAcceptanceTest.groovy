package pl.cezarysanecki.parkingdomain.requestingreservation.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.ParkingConfig
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews
import pl.cezarysanecki.parkingdomain.requestingreservation.RequestingReservationConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForWholeParkingSpot
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientId
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationRequestsViews

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle.ParkOnReservedCommand

@ActiveProfiles("local")
@SpringBootTest(classes = [
    RequestingReservationConfig.class, ParkingConfig.class,
    EventPublisherTestConfig.class])
class AllowingToParkOnReservedParkingSpotAcceptanceTest extends AbstractReservingAcceptanceTest {
  
  @Autowired
  RequestingReservationForWholeParkingSpot reservingWholeParkingSpot
  @Autowired
  ParkingVehicle parkingVehicle
  
  @Autowired
  VehicleViews vehicleViews
  @Autowired
  ParkingSpotReservationRequestsViews parkingSpotReservationsViews
  
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
    def result = reservingWholeParkingSpot.requestReservation(new RequestingReservationForWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  private void thereIsReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .any {
          it.parkingSpotId == parkingSpotId.value
              && it.currentReservationRequests.contains(reservationId.value)
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
