package pl.cezarysanecki.parkingdomain.parking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import pl.cezarysanecki.parkingdomain.commons.aggregates.AggregateRootIsStale;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId;
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Profile("integration")
@SpringBootTest
class OptimisticLockingForOccupationCaseIntTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16.4"
  );

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

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
  void checkIfOptimisticLockingIsWorkingCorrectlyForOccupation() {
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

    //then
    assertThatThrownBy(() -> occupationRepository.saveCheckingVersion(
        new Occupation(
            new OccupationId(UUID.randomUUID()),
            secondOccupant,
            parkingSpot,
            new SpotUnits(2))))
        .isInstanceOf(AggregateRootIsStale.class);
  }

}
