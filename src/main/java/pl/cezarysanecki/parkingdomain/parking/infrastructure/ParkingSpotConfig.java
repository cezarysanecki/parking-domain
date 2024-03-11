package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parking.application.ParkingOnParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.application.ReleasingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

@Configuration
public class ParkingSpotConfig {

    @Bean
    public ParkingOnParkingSpot parkingOnParkingSpot(ParkingSpots parkingSpots) {
        return new ParkingOnParkingSpot(parkingSpots);
    }

    @Bean
    public ReleasingParkingSpot releasingParkingSpot(ParkingSpots parkingSpots) {
        return new ReleasingParkingSpot(parkingSpots);
    }

    @Bean
    @Profile("local")
    public ParkingSpots inMemoryParkingSpotRepository() {
        return new InMemoryParkingSpotRepository();
    }

}
