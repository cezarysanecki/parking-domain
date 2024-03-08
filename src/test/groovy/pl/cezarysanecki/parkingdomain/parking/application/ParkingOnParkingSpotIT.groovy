package pl.cezarysanecki.parkingdomain.parking.application

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.parking.infrastructure.ParkingSpotConfig
import spock.lang.Specification

import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.vehicleWithSize

@ActiveProfiles("local")
@SpringBootTest(classes = [ParkingSpotConfig.class])
class ParkingOnParkingSpotIT extends Specification {
  
  @Autowired
  ParkingOnParkingSpot parkingOnParkingSpot
  
  def tas() {
    when:
      parkingOnParkingSpot.park(new ParkVehicleCommand(anyParkingSpotId(), vehicleWithSize(1)))
    
    then:
      true
  }
  
}
