package pl.cezarysanecki.parkingdomain.reservation.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.application.ClientReservationsEventHandler;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.application.ParkingSpotEventsHandler;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    ClientReservationsEventHandler clientReservationsEventHandler(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new ClientReservationsEventHandler(parkingSpotReservationsRepository);
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
