package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.CancellingParkingSpotRequestReservationEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.StoringParkingSpotReservationRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.CreatingParkingSpotReservationRequestsEventsHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    StoringParkingSpotReservationRequestEventHandler savingParkingSpotReservationRequestEventHandler(ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository) {
        return new StoringParkingSpotReservationRequestEventHandler(parkingSpotReservationRequestsRepository);
    }

    @Bean
    CancellingParkingSpotRequestReservationEventHandler cancellingParkingSpotRequestReservationEventHandler(ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository) {
        return new CancellingParkingSpotRequestReservationEventHandler(parkingSpotReservationRequestsRepository);
    }

    @Bean
    CreatingParkingSpotReservationRequestsEventsHandler creatingParkingSpotReservationRequestsEventsHandler(ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository) {
        return new CreatingParkingSpotReservationRequestsEventsHandler(parkingSpotReservationRequestsRepository);
    }

    @Bean
    @Profile("local")
    ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository() {
        return new InMemoryParkingSpotReservationRequestsRepository(eventPublisher);
    }

}
