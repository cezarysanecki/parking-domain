package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure

import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.OpenParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpot
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotConfig.class])
class ParkingSpotsDatabaseIT extends Specification {
  
  ParkingSpotId parkingSpotId = ParkingSpotId.newOne()
  
  @MockBean
  EventPublisher eventPublisher
  
  @Autowired
  ParkingSpots parkingSpots
  
  def "persistence of parking spot in real database should work"() {
    when:
      parkingSpots.publish(parkingSpotCreated(parkingSpotId))
    
    then:
      parkingSpotIsPersistedAs(OpenParkingSpot.class, parkingSpotId)
  }
  
  private ParkingSpotCreated parkingSpotCreated(ParkingSpotId parkingSpotId) {
    return new ParkingSpotCreated(parkingSpotId, ParkingSpotCapacity.of(4), ParkingSpotCategory.Gold)
  }
  
  void parkingSpotIsPersistedAs(Class<?> clz, ParkingSpotId parkingSpotId) {
    ParkingSpot foundParkingSpot = loadPersistedParkingSpot(parkingSpotId)
    assert foundParkingSpot.class == clz
  }
  
  ParkingSpot loadPersistedParkingSpot(ParkingSpotId parkingSpotId) {
    Option<ParkingSpot> loaded = parkingSpots.findOpenBy(parkingSpotId)
    ParkingSpot parkingSpot = loaded.getOrElseThrow({
      new IllegalStateException("should have been persisted")
    })
    return parkingSpot
  }
  
}
