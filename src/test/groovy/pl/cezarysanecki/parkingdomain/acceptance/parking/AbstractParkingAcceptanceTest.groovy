package pl.cezarysanecki.parkingdomain.acceptance.parking

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.commons.commands.Result
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.management.client.ClientId
import pl.cezarysanecki.parkingdomain.management.client.RegisteringClient
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.infrastructure.ParkingSpotConfig
import pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.RequestingReservationConfig
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest(classes = [
    ParkingSpotConfig.class,
    RequestingReservationConfig.class,
    EventPublisherTestConfig.class])
abstract class AbstractParkingAcceptanceTest extends Specification {
  
  @Autowired
  AddingParkingSpot creatingParkingSpot
  @Autowired
  RegisteringClient registeringClient
  
  ParkingSpotId addParkingSpot(int capacity, ParkingSpotCategory category) {
    def result = creatingParkingSpot.addParkingSpot(capacity, category)
    return (result.get() as Result.Success<ParkingSpotId>).getResult()
  }
  
  ClientId registerClient(String phoneNumber) {
    def result = registeringClient.registerClient(phoneNumber)
    return (result.get() as Result.Success<ClientId>).getResult()
  }
  
}
