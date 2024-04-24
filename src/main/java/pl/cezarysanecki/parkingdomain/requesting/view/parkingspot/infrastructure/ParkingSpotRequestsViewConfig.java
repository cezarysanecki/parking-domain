package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotRequestsViewConfig {

    @Bean
    @Profile("local")
    ParkingSpotRequestsViews parkingSpotRequestsViews() {
        return new InMemoryParkingSpotRequestsViewRepository();
    }

}
