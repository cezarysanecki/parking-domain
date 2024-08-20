package pl.cezarysanecki.parkingdomain;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories;
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

@SpringBootTest
@ActiveProfiles("local")
public abstract class BaseAcceptanceTest {

  @Autowired
  ParkingSpotFacade parkingSpotFacade;

  @Autowired
  ClientFacade clientFacade;

  @BeforeEach
  void setup() {
    InMemoryRepositories.clearAll();
  }

  ParkingSpotId addParkingSpot(ParkingSpotCapacity capacity, ParkingSpotCategory category) {
    Try<ParkingSpotId> parkingSpotId = parkingSpotFacade.addParkingSpot(capacity, category);
    return parkingSpotId.get();
  }

  ClientId registerClient(ClientType clientType, PhoneNumber phoneNumber) {
    Try<ClientId> clientId = clientFacade.registerClient(clientType, phoneNumber);
    return clientId.get();
  }

}
