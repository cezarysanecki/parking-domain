package pl.cezarysanecki.parkingdomain.parking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.UUID;

@SpringBootTest(classes = {
    ParkingConfig.class,
    ProdOccupantRepository.class,
    ProdOccupationRepository.class,
    ProdParkingRepository.class,
    ProdReservedOccupationRepository.class
})
class OptimisticLockingForOccupationCaseIntTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16.4"
  );

  @Autowired
  ParkingRepository parkingRepository;
  @Autowired
  OccupantRepository occupantRepository;
  @Autowired
  OccupationRepository occupationRepository;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Test
  void shouldGetCustomers() {
    //given
    ParkingSpotId parkingSpotId = new ParkingSpotId(UUID.randomUUID());
    ClientId firstClientId = new ClientId(UUID.randomUUID());
    ClientId secondClientId = new ClientId(UUID.randomUUID());
    //and
    parkingRepository.saveNew(ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity()));
    occupantRepository.saveNew(Occupant.newOne(firstClientId));
    occupantRepository.saveNew(Occupant.newOne(secondClientId));

    //when
    ParkingSpot parkingSpot = parkingRepository.loadBy(parkingSpotId);
    Occupant firstOccupant = occupantRepository.findBy(new OccupantId(firstClientId.value()));
    Occupant secondOccupant = occupantRepository.findBy(new OccupantId(secondClientId.value()));
    //and
    occupationRepository.saveCheckingVersion(new Occupation(
        new OccupationId(UUID.randomUUID()),
        firstOccupant,
        parkingSpot,
        new SpotUnits(2)));
    //and
    occupationRepository.saveCheckingVersion(new Occupation(
        new OccupationId(UUID.randomUUID()),
        secondOccupant,
        parkingSpot,
        new SpotUnits(2)));

    //then
    // ???
  }


}
