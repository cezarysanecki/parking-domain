package pl.cezarysanecki.parkingdomain._local;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
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
    parkingSpotFacade.addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    parkingSpotFacade.addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Silver);
    parkingSpotFacade.addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Bronze);

    clientFacade.registerClient(ClientType.INDIVIDUAL, PhoneNumber.of("123123123"));
    clientFacade.registerClient(ClientType.INDIVIDUAL, PhoneNumber.of("321321321"));
    clientFacade.registerClient(ClientType.BUSINESS, PhoneNumber.of("789789789"));
  }
}
