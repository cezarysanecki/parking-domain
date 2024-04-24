package pl.cezarysanecki.parkingdomain.parking.view.parkingspot.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parking.view.parkingspot.model.ParkingSpotViews;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotViewConfig {

    @Bean
    @Profile("local")
    InMemoryParkingSpotViewRepository parkingSpotViews() {
        return new InMemoryParkingSpotViewRepository();
    }

}
