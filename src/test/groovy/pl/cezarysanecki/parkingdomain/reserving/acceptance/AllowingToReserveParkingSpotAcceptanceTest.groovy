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
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import pl.cezarysanecki.parkingdomain.reserving.ReservingConfig
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingPartOfParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.application.ReservingWholeParkingSpot
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientId
import pl.cezarysanecki.parkingdomain.reserving.client.model.ReservationId
import pl.cezarysanecki.parkingdomain.reserving.view.parkingspot.model.ParkingSpotReservationsViews
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ReservingConfig.class, ParkingConfig.class,
    EventPublisherTestConfig.class])
class AllowingToReserveParkingSpotAcceptanceTest extends Specification {
  
  @Autowired
  ReservingWholeParkingSpot reservingWholeParkingSpot
  @Autowired
  ReservingPartOfParkingSpot reservingPartOfParkingSpot
  
  @Autowired
  CreatingParkingSpot creatingParkingSpot
  @Autowired
  ParkingSpotReservationsViews parkingSpotReservationsViews
  
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
  
  private ParkingSpotId createParkingSpot(int capacity) {
    def result = creatingParkingSpot.create(new CreatingParkingSpot.Command(
        ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold))
    return (result.get() as Result.Success<ParkingSpotId>).getResult()
  }
  
  private ReservationId reserveWholeParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId) {
    def result = reservingWholeParkingSpot.requestReservation(new ReservingWholeParkingSpot.Command(
        clientId, parkingSpotId))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  private ReservationId reservePartOfParkingSpotFor(ClientId clientId, ParkingSpotId parkingSpotId, int vehicleSize) {
    def result = reservingPartOfParkingSpot.requestReservation(new ReservingPartOfParkingSpot.Command(
        clientId, parkingSpotId, VehicleSize.of(vehicleSize)))
    return (result.get() as Result.Success<ReservationId>).getResult()
  }
  
  void thereIsReservation(ParkingSpotId parkingSpotId, ReservationId reservationId) {
    assert parkingSpotReservationsViews.getAllParkingSpots()
        .any {
          it.parkingSpotId == parkingSpotId.value
              && it.currentReservations.contains(reservationId.value)
        }
  }
  
}
