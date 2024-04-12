package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.OpenParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupied

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotConfig.class])
class ParkingSpotsDatabaseIT extends Specification {
  
  ParkingSpotId parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpots parkingSpots
  
  def "persistence of parking spot in real database should work"() {
    given:
      def vehicleSize = 4
    
    when:
      parkingSpots.publish(parkingSpotCreated(parkingSpotId, vehicleSize))
    then:
      parkingSpotShouldBeFoundInDatabaseBeingEmpty(parkingSpotId)
    
    when:
      parkingSpots.publish(occupyParkingSpot(parkingSpotId, vehicleSize))
    then:
      parkingSpotShouldBeFoundInDatabaseBeingFull(parkingSpotId)
  }
  
  private ParkingSpotCreated parkingSpotCreated(ParkingSpotId parkingSpotId, int capacity) {
    return new ParkingSpotCreated(parkingSpotId, ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold)
  }
  
  private ParkingSpotOccupied occupyParkingSpot(ParkingSpotId parkingSpotId, int vehicleSize) {
    return new ParkingSpotOccupied(parkingSpotId, VehicleId.newOne(), VehicleSize.of(vehicleSize))
  }
  
  private void parkingSpotShouldBeFoundInDatabaseBeingEmpty(ParkingSpotId parkingSpotId) {
    def openParkingSpot = loadPersistedOpenParkingSpot(parkingSpotId)
    assert openParkingSpot.getParkingSpotOccupation().isEmpty()
  }
  
  private void parkingSpotShouldBeFoundInDatabaseBeingFull(ParkingSpotId parkingSpotId) {
    def openParkingSpot = loadPersistedOpenParkingSpot(parkingSpotId)
    assert openParkingSpot.getParkingSpotOccupation().isFull()
  }
  
  OpenParkingSpot loadPersistedOpenParkingSpot(ParkingSpotId parkingSpotId) {
    Option<OpenParkingSpot> loaded = parkingSpots.findOpenBy(parkingSpotId)
    OpenParkingSpot openParkingSpot = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return openParkingSpot
  }
  
}
