package pl.cezarysanecki.parkingdomain.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ReservationConfig {

  private final ReservationRepository reservationRepository;
  private final EventPublisher eventPublisher;

  @Bean
  ReservationFacade reservationFacade() {
    return new ReservationFacade(
        reservationRepository,
        eventPublisher);
  }

  @Bean
  ReservationEventHandler reservationEventHandler() {
    return new ReservationEventHandler(reservationRepository);
  }

}

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalReservationConfig {

  @Bean
  InMemoryReservationRepository inMemoryReservationRepository() {
    return new InMemoryReservationRepository();
  }

}
