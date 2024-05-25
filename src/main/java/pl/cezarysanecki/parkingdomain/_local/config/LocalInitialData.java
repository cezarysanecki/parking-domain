package pl.cezarysanecki.parkingdomain._local.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.RegisteringClient;
import pl.cezarysanecki.parkingdomain.management.parkingspot.AddingParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
class LocalInitialData implements CommandLineRunner {

  private final AddingParkingSpot addingParkingSpot;
  private final RegisteringClient registeringClient;

  @Override
  public void run(final String... args) {
    addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Gold);
    addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Silver);
    addingParkingSpot.addParkingSpot(4, ParkingSpotCategory.Bronze);

    registeringClient.registerClient(ClientType.INDIVIDUAL, "123123123");
    registeringClient.registerClient(ClientType.INDIVIDUAL, "321321321");
    registeringClient.registerClient(ClientType.BUSINESS, "789789789");
  }
}
