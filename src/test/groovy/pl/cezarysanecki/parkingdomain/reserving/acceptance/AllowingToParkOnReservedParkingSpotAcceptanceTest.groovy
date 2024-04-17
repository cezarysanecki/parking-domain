package pl.cezarysanecki.parkingdomain.reserving.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.ParkingConfig
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.parking.view.vehicle.model.VehicleViews
import pl.cezarysanecki.parkingdomain.reserving.ReservingConfig
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingWholeParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.view.parkingspot.model.ParkingSpotReservationsViews
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.vehicle.application.ParkingVehicle.ParkOnReservedCommand

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ReservingConfig.class, ParkingConfig.class,
    EventPublisherTestConfig.class])
class AllowingToParkOnReservedParkingSpotAcceptanceTest extends Specification {
  
  @Autowired
  ReservingWholeParkingSpot reservingWholeParkingSpot
  @Autowired
  ParkingVehicle parkingVehicle
  
  @Autowired
  RegisteringVehicle registeringVehicle
  @Autowired
  CreatingParkingSpot creatingParkingSpot
  @Autowired
  VehicleViews vehicleViews
  @Autowired
  ParkingSpotReservationsViews parkingSpotReservationsViews
  
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
  
  private ParkingSpotId createParkingSpot(int capacity) {
    def result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
        ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold))
    return (result.get() as Result.Success<ParkingSpotId>).getResult()
  }
  
  private VehicleId registerVehicle(int size) {
    def result = registeringVehicle.register(new RegisteringVehicle.Command(VehicleSize.of(size)))
    return (result.get() as Result.Success<VehicleId>).getResult()
  }
  
  private ReservationId reserveWholeParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = reservingWholeParkingSpot.requestReservation(new ReservingWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  private void thereIsReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .any {
          it.parkingSpotId == parkingSpotId.value
              && it.currentReservations.contains(reservationId.value)
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
