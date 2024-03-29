package pl.cezarysanecki.parkingdomain.parkingspot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotCapacity;

@Slf4j
@Profile("local")
@Configuration
class ParkingSpotLocalConfig {

    @Bean
    CommandLineRunner init(CreatingParkingSpot creatingParkingSpot) {
        return args -> {
            creatingParkingSpot.create(new CreatingParkingSpot.Command(ParkingSpotCapacity.of(4)));
            creatingParkingSpot.create(new CreatingParkingSpot.Command(ParkingSpotCapacity.of(4)));
            creatingParkingSpot.create(new CreatingParkingSpot.Command(ParkingSpotCapacity.of(4)));
        };
    }

}
