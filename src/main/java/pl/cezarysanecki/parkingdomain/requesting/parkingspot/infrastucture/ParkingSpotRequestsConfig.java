package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.CancellingParkingSpotRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.CreatingParkingSpotRequestsEventsHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.application.StoringParkingSpotRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    StoringParkingSpotRequestEventHandler storingParkingSpotRequestEventHandler(
            ParkingSpotRequestsRepository parkingSpotRequestsRepository
    ) {
        return new StoringParkingSpotRequestEventHandler(parkingSpotRequestsRepository);
    }

    @Bean
    CancellingParkingSpotRequestEventHandler cancellingParkingSpotRequestEventHandler(
            ParkingSpotRequestsRepository parkingSpotRequestsRepository
    ) {
        return new CancellingParkingSpotRequestEventHandler(parkingSpotRequestsRepository);
    }

    @Bean
    CreatingParkingSpotRequestsEventsHandler creatingParkingSpotRequestsEventsHandler(
            ParkingSpotRequestsRepository parkingSpotRequestsRepository
    ) {
        return new CreatingParkingSpotRequestsEventsHandler(parkingSpotRequestsRepository);
    }

    @Bean
    @Profile("local")
    InMemoryParkingSpotRequestsRepository parkingSpotRequestsRepository() {
        return new InMemoryParkingSpotRequestsRepository(eventPublisher);
    }

}
