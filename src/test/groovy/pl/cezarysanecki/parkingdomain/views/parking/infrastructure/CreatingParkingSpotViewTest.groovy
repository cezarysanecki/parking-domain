package pl.cezarysanecki.parkingdomain.views.parking.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle
import pl.cezarysanecki.parkingdomain.views.parking.model.AvailableParkingSpotView
import pl.cezarysanecki.parkingdomain.views.parking.model.ParkingViews
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWith
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType.Gold

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingViewConfig.class, EventPublisherTestConfig.class])
class CreatingParkingSpotViewTest extends Specification {
  
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ParkingViews parkingViews
  
  def "reservation request is erased if reservation is cancelled"() {
    when:
      eventPublisher.publish parkingSpotCreated(4)
    then:
      thereIsParkingSpotWithLeftCapacity(parkingSpotId, 4)
    
    when:
      eventPublisher.publish vehicleParked(vehicleWith(2))
    then:
      thereIsParkingSpotWithLeftCapacity(parkingSpotId, 2)
    
    when:
      eventPublisher.publish vehicleParked(vehicleWith(2))
    then:
      parkingSpotIsAbsent(parkingSpotId)
    
    when:
      eventPublisher.publish vehicleLeft(vehicleWith(2))
    then:
      thereIsParkingSpotWithLeftCapacity(parkingSpotId, 2)
    
    when:
      eventPublisher.publish vehicleLeft(vehicleWith(2))
    then:
      thereIsParkingSpotWithLeftCapacity(parkingSpotId, 4)
  }
  
  void thereIsParkingSpotWithLeftCapacity(ParkingSpotId parkingSpotId, int leftCapacity) {
    def clientReservationsView = loadPersistedAvailableParkingSpotView(parkingSpotId).get()
    assert clientReservationsView.parkingSpotId == parkingSpotId
    assert clientReservationsView.leftCapacity == leftCapacity
  }
  
  void parkingSpotIsAbsent(ParkingSpotId parkingSpotId) {
    def clientReservationsView = loadPersistedAvailableParkingSpotView(parkingSpotId)
    assert clientReservationsView.isEmpty()
  }
  
  ParkingSpotCreated parkingSpotCreated(int capacity) {
    return new ParkingSpotCreated(parkingSpotId, Gold, capacity)
  }
  
  VehicleLeft vehicleLeft(Vehicle vehicle) {
    return new VehicleLeft(parkingSpotId, vehicle)
  }
  
  VehicleParked vehicleParked(Vehicle vehicle) {
    return new VehicleParked(parkingSpotId, vehicle)
  }
  
  Option<AvailableParkingSpotView> loadPersistedAvailableParkingSpotView(ParkingSpotId parkingSpotId) {
    return Option.ofOptional(
        parkingViews.findAvailable().availableParkingSpots
            .stream()
            .filter { it.parkingSpotId == parkingSpotId }
            .findFirst())
  }
  
}
