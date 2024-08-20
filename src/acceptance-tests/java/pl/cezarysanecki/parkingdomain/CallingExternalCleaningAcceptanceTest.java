package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.cezarysanecki.parkingdomain.cleaning.usecase.CallingCleaningWhenSpotsDirtyUseCase;
import pl.cezarysanecki.parkingdomain.commons.Result;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class CallingExternalCleaningAcceptanceTest extends BaseAcceptanceTest {

  @Autowired
  ParkingFacade parkingFacade;
  @Autowired
  CallingCleaningWhenSpotsDirtyUseCase callingCleaningWhenSpotsDirtyUseCase;

  @Test
  void callCleaningIfThereAreRequiredNumberOfDirtyParkingSpots() {
    //given
    ParkingSpotId firstParkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Silver);
    ParkingSpotId secondParkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Silver);
    ClientId clientId = registerClient(ClientType.INDIVIDUAL, RandomTestUtils.randomPhoneNumber());

    //when
    IntStream.range(0, 10)
        .forEach(i -> {
          OccupationId occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), firstParkingSpotId, new SpotUnits(4)).get();
          parkingFacade.release(occupationId);
        });
    IntStream.range(0, 10)
        .forEach(i -> {
          OccupationId occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), secondParkingSpotId, new SpotUnits(4)).get();
          parkingFacade.release(occupationId);
        });
    //and
    Result result = callingCleaningWhenSpotsDirtyUseCase.run();

    //then
    assertThat(result).isEqualTo(Result.Success);
  }


}
