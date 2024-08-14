package pl.cezarysanecki.parkingdomain.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ParkingConfig {

  private final ParkingRepository parkingRepository;
  private final OccupationRepository occupationRepository;
  private final OccupantRepository occupantRepository;
  private final ReservationRepository reservationRepository;
  private final DateProvider dateProvider;
  private final EventPublisher eventPublisher;

  @Bean
  ParkingFacade parkingFacade(
      @Value("${business.parking.minutesToConsiderReservationActive}") int minutesToConsiderReservationActive
  ) {
    return new ParkingFacade(
        parkingRepository,
        occupationRepository,
        occupantRepository,
        dateProvider,
        eventPublisher,
        minutesToConsiderReservationActive);
  }

  @Bean
  ParkingEventHandler parkingEventHandler() {
    return new ParkingEventHandler(
        parkingRepository,
        occupantRepository,
        reservationRepository);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalParkingConfig {

  @Bean
  InMemoryParkingRepository inMemoryParkingRepository() {
    return new InMemoryParkingRepository();
  }

  @Bean
  InMemoryOccupationRepository inMemoryOccupationRepository() {
    return new InMemoryOccupationRepository();
  }

  @Bean
  InMemoryOccupantRepository inMemoryOccupantRepository() {
    return new InMemoryOccupantRepository();
  }

  @Bean
  InMemoryReservationRepository inMemoryReservationRepository() {
    return new InMemoryReservationRepository();
  }

}
