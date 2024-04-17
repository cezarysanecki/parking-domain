package pl.cezarysanecki.parkingdomain.reserving.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.application.HandlingClientCancelledReservationEventHandler;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.application.HandlingClientReservationsEventHandler;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.application.ParkingSpotEventsHandler;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    HandlingClientReservationsEventHandler handlingClientReservationsEventHandler(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new HandlingClientReservationsEventHandler(parkingSpotReservationsRepository);
    }

    @Bean
    HandlingClientCancelledReservationEventHandler handlingClientCancelledReservationEventHandler(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new HandlingClientCancelledReservationEventHandler(parkingSpotReservationsRepository);
    }

    @Bean
    ParkingSpotEventsHandler parkingSpotEventsHandlerForReservingParkingSpot(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new ParkingSpotEventsHandler(parkingSpotReservationsRepository);
    }

    @Bean
    @Profile("local")
    ParkingSpotReservationsRepository parkingSpotReservationsRepository() {
        return new InMemoryParkingSpotReservationsRepository(eventPublisher);
    }

}
