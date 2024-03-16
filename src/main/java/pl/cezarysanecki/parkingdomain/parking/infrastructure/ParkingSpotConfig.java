package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.application.ParkingOnParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ParkingSpotConfig {

    private final DateProvider dateProvider;
    private final EventPublisher eventPublisher;

    @Bean
    public ParkingOnParkingSpot parkingOnParkingSpot(ParkingSpots parkingSpots) {
        return new ParkingOnParkingSpot(parkingSpots);
    }

    @Bean
    public ReleasingParkingSpot releasingParkingSpot(ParkingSpots parkingSpots) {
        return new ReleasingParkingSpot(parkingSpots);
    }

    @Bean
    ReservingParkingSpots reservingParkingSpots() {
        return new ReservingParkingSpots(dateProvider, eventPublisher);
    }

    @Bean
    @Profile("local")
    public ParkingSpots inMemoryParkingSpotRepository() {
        return new InMemoryParkingSpotRepository(eventPublisher);
    }

    @Profile("local")
    @Bean
    CommandLineRunner init(ParkingSpots parkingSpots) {
        return args -> {
            parkingSpots.publish(new ParkingSpotCreated(ParkingSpotId.of(UUID.randomUUID()), 4));
            parkingSpots.publish(new ParkingSpotCreated(ParkingSpotId.of(UUID.randomUUID()), 4));
            parkingSpots.publish(new ParkingSpotCreated(ParkingSpotId.of(UUID.randomUUID()), 4));
        };
    }

}
