package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.HandlingClientCancelledReservationEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.HandlingClientReservationsEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.ParkingSpotEventsHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    HandlingClientReservationsEventHandler handlingClientReservationsEventHandler(ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository) {
        return new HandlingClientReservationsEventHandler(parkingSpotReservationRequestsRepository);
    }

    @Bean
    HandlingClientCancelledReservationEventHandler handlingClientCancelledReservationEventHandler(ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository) {
        return new HandlingClientCancelledReservationEventHandler(parkingSpotReservationRequestsRepository);
    }

    @Bean
    ParkingSpotEventsHandler parkingSpotEventsHandlerForReservingParkingSpot(ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository) {
        return new ParkingSpotEventsHandler(parkingSpotReservationRequestsRepository);
    }

    @Bean
    @Profile("local")
    ParkingSpotReservationRequestsRepository parkingSpotReservationsRepository() {
        return new InMemoryParkingSpotReservationRequestsRepository(eventPublisher);
    }

}
