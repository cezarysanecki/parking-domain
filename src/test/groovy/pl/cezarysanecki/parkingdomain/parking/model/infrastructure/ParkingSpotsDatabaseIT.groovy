package pl.cezarysanecki.parkingdomain.parking.model.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpot
import pl.cezarysanecki.parkingdomain.shared.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpots
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId
import pl.cezarysanecki.parkingdomain.parking.model.model.SpotUnits
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.model.ParkingSpotEvent.Occupied

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
  
  private ParkingSpotAdded parkingSpotCreated(ParkingSpotId parkingSpotId, int capacity) {
    return new ParkingSpotAdded(parkingSpotId, ParkingSpotCapacity.of(capacity), ParkingSpotCategory.Gold)
  }
  
  private Occupied occupyParkingSpot(ParkingSpotId parkingSpotId, int vehicleSize) {
    return new Occupied(parkingSpotId, VehicleId.newOne(), SpotUnits.of(vehicleSize))
  }
  
  private void parkingSpotShouldBeFoundInDatabaseBeingEmpty(ParkingSpotId parkingSpotId) {
    def openParkingSpot = loadPersistedOpenParkingSpot(parkingSpotId)
    assert openParkingSpot.spotOccupation.isFull()
  }
  
  private void parkingSpotShouldBeFoundInDatabaseBeingFull(ParkingSpotId parkingSpotId) {
    def openParkingSpot = loadPersistedOpenParkingSpot(parkingSpotId)
    assert openParkingSpot.spotOccupation.isFull()
  }
  
  ParkingSpot loadPersistedOpenParkingSpot(ParkingSpotId parkingSpotId) {
    Option<ParkingSpot> loaded = parkingSpots.findBy(parkingSpotId)
    ParkingSpot openParkingSpot = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return openParkingSpot
  }
  
}
