package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestingParkingSpotAcceptanceTest extends BaseAcceptanceTest {

  @Autowired
  RequestingFacade requestingFacade;

  @Test
  void cannotRequestParkingSpotIfCapacityIsExceeded() {
    //given
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId firstClientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    ClientId secondclientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    TimeSlot timeSlot = TimeSlot.create(LocalDate.of(2020, 10, 10), 10, 15);

    //when
    requestingFacade.createForAll(timeSlot);
    requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4));
    Optional<RequestId> result = requestingFacade.request(new RequesterId(secondclientId.value()), parkingSpotId, timeSlot, new SpotUnits(4));

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void canRequestParkingSpotIfPreviousRequestHasBeenCancelled() {
    //given
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId firstClientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    ClientId secondclientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    TimeSlot timeSlot = TimeSlot.create(LocalDate.of(2020, 10, 10), 10, 15);

    //when
    requestingFacade.createForAll(timeSlot);
    RequestId request = requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4)).get();
    requestingFacade.cancel(request);
    Optional<RequestId> result = requestingFacade.request(new RequesterId(secondclientId.value()), parkingSpotId, timeSlot, new SpotUnits(4));

    //then
    assertThat(result).isPresent();
  }

  @Test
  void individualClientCanHaveOnlyOneRequest() {
    //given
    ParkingSpotId firstParkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ParkingSpotId secondParkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId individualClientId = registerClient(ClientType.INDIVIDUAL, RandomTestUtils.randomPhoneNumber());
    TimeSlot timeSlot = TimeSlot.create(LocalDate.of(2020, 10, 10), 10, 15);

    //when
    requestingFacade.createForAll(timeSlot);
    requestingFacade.request(new RequesterId(individualClientId.value()), firstParkingSpotId, timeSlot, new SpotUnits(4));
    Optional<RequestId> result = requestingFacade.request(new RequesterId(individualClientId.value()), secondParkingSpotId, timeSlot, new SpotUnits(4));

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void businessClientCanHaveMultipleRequests() {
    //given
    ParkingSpotId firstParkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ParkingSpotId secondParkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId individualClientId = registerClient(ClientType.BUSINESS, RandomTestUtils.randomPhoneNumber());
    TimeSlot timeSlot = TimeSlot.create(LocalDate.of(2020, 10, 10), 10, 15);

    //when
    requestingFacade.createForAll(timeSlot);
    requestingFacade.request(new RequesterId(individualClientId.value()), firstParkingSpotId, timeSlot, new SpotUnits(4));
    Optional<RequestId> result = requestingFacade.request(new RequesterId(individualClientId.value()), secondParkingSpotId, timeSlot, new SpotUnits(4));

    //then
    assertThat(result).isPresent();
  }

}
