package pl.cezarysanecki.parkingdomain.parkingspot.view.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parkingspot.view.model.ParkingSpotViews;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotViewConfig {

    @Bean
    @Profile("local")
    ParkingSpotViews parkingSpotViews() {
        return new InMemoryParkingSpotViewRepository();
    }

}
