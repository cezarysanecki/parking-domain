package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.BeforeEach;
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
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CannotOccupyReservedParkingSpotAcceptanceTest extends BaseAcceptanceTest {

  @Autowired
  LocalDateProvider dateProvider;

  @Autowired
  ParkingFacade parkingFacade;
  @Autowired
  RequestingFacade requestingFacade;
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase;
  @Autowired
  OccupyUsingReservationUseCase occupyUsingReservationUseCase;

  private static final LocalDate CURRENT_DATE = LocalDate.of(2020, 10, 10);

  @BeforeEach
  void setup() {
    dateProvider.setCurrentDate(CURRENT_DATE);
  }

  @Test
  void cannotOccupyParkingSpotIfThereIsReservation() {
    //given
    ParkingSpotId parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Gold);
    ClientId firstClientId = registerClient(ClientType.INDIVIDUAL, RandomTestUtils.randomPhoneNumber());
    ClientId secondclientId = registerClient(ClientType.INDIVIDUAL, RandomTestUtils.randomPhoneNumber());
    TimeSlot timeSlot = TimeSlot.create(LocalDate.of(2020, 10, 10), 10, 15);

    //when
    requestingFacade.createForAll(timeSlot);
    requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4)).get();
    requestingFacade.makeValidFor(CURRENT_DATE);
    //and
    dateProvider.passHours(9);
    dateProvider.passMinutes(1);
    activatingReservationsUseCase.run();
    //and
    Optional<OccupationId> result = parkingFacade.occupy(new OccupantId(secondclientId.value()), parkingSpotId, new SpotUnits(4));

    //then
    assertThat(result).isEmpty();
  }

}
