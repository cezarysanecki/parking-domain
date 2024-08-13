package pl.cezarysanecki.parkingdomain._local.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
class LocalInitialData implements CommandLineRunner {

  private final ParkingSpotFacade parkingSpotFacade;
  private final ClientFacade clientFacade;

  @Override
  public void run(final String... args) {
    parkingSpotFacade.addParkingSpot(4, ParkingSpotCategory.Gold);
    parkingSpotFacade.addParkingSpot(4, ParkingSpotCategory.Silver);
    parkingSpotFacade.addParkingSpot(4, ParkingSpotCategory.Bronze);

    clientFacade.registerClient(ClientType.INDIVIDUAL, "123123123");
    clientFacade.registerClient(ClientType.INDIVIDUAL, "321321321");
    clientFacade.registerClient(ClientType.BUSINESS, "789789789");
  }
}
