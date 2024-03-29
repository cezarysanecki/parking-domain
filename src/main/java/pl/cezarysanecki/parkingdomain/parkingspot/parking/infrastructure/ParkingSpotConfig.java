package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.OccupyingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpots;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotConfig {

    private final EventPublisher eventPublisher;

    @Bean
    public OccupyingParkingSpot occupyingParkingSpot(ParkingSpots parkingSpots) {
        return new OccupyingParkingSpot(parkingSpots);
    }

    @Bean
    public ReleasingParkingSpot releasingParkingSpot(ParkingSpots parkingSpots) {
        return new ReleasingParkingSpot(parkingSpots);
    }

    @Bean
    @Profile("local")
    public ParkingSpots parkingSpots() {
        return new InMemoryParkingSpotRepository(eventPublisher);
    }

}
