package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.CancellingParkingSpotRequestReservationEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.StoringParkingSpotReservationRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.CreatingParkingSpotReservationRequestsEventsHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationRequestsConfig {

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
