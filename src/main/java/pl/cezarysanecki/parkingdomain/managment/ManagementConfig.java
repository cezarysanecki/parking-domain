package pl.cezarysanecki.parkingdomain.managment;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Configuration
@RequiredArgsConstructor
class ManagementConfig {

    private final EventPublisher eventPublisher;

    @Bean
    ParkingManagement parkingManagement() {
        return new ParkingManagement(eventPublisher);
    }

    @Profile("local")
    @Bean
    CommandLineRunner init(ParkingManagement parkingManagement) {
        return args -> {
            parkingManagement.addParkingSpot();
            parkingManagement.addParkingSpot();
            parkingManagement.addParkingSpot();
        };
    }

}
