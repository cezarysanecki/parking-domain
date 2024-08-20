package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

public class OccupyingParkingSpotAcceptanceTest extends BaseAcceptanceTest {

  @Autowired
  ParkingFacade parkingFacade;

  @Test
  void cannotOccupyParkingSpotIfCapacityIsExceeded() {
    //given
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId firstClientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    ClientId secondclientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());

    //when
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

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void canOccupyParkingSpotIfOccupationHasBeenReleased() {
    //given
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId firstClientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    ClientId secondclientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());

    //when
    OccupationId occupation = parkingFacade.occupy(
        new OccupantId(firstClientId.value()),
        parkingSpotId,
        new SpotUnits(4)
    ).get();
    parkingFacade.release(occupation);
    //and
    Optional<OccupationId> result = parkingFacade.occupy(
        new OccupantId(secondclientId.value()),
        parkingSpotId,
        new SpotUnits(4)
    );

    //then
    assertThat(result).isPresent();
  }

}
