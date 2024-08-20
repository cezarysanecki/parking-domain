package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.cezarysanecki.parkingdomain._local.LocalDateProvider;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased;
import pl.cezarysanecki.parkingdomain.parking.usecase.RemoveOccupationByForceUseCase;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import static org.assertj.core.api.Assertions.assertThat;

public class RemovingOccupationUsingForceTest extends BaseAcceptanceTest {

  @Autowired
  LocalDateProvider dateProvider;

  @Autowired
  ParkingFacade parkingFacade;
  @Autowired
  RemoveOccupationByForceUseCase removeOccupationByForceUseCase;

  @Test
  void oneClientCanRequestReservationForOtherOneToOccupyParkingSpot() {
    //given
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId clientId = registerClient(ClientType.INDIVIDUAL, RandomTestUtils.randomPhoneNumber());

    //when
    OccupationId occupation = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).get();
    boolean result = removeOccupationByForceUseCase.run(occupation, ParkingSpotForceReleased.Reason.NOT_RELEASED_PARKING_SPOT);

    //then
    assertThat(result).isTrue();
  }

}
