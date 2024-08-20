package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
public class OccupyingParkingSpotAcceptanceTest extends BaseAcceptanceTest {

  @Autowired
  ParkingFacade parkingFacade;

  @Test
  void cannotOccupyParkingSpotIfCapacityIsExceeded() {
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);

    ClientId firstClientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    ClientId secondclientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());

    parkingFacade.occupy(
        new OccupantId(firstClientId.value()),
        parkingSpotId,
        new SpotUnits(4)
    );
    Optional<OccupationId> result = parkingFacade.occupy(
        new OccupantId(secondclientId.value()),
        parkingSpotId,
        new SpotUnits(1)
    );

    assertThat(result).isEmpty();
  }

}
