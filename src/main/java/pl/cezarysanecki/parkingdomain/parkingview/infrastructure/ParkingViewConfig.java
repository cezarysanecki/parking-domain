package pl.cezarysanecki.parkingdomain.parkingview.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parkingview.model.ParkingViews;

@Configuration
public class ParkingViewConfig {

    @Bean
    @Profile("local")
    public ParkingViews inMemoryParkingViewsRepository() {
        return new InMemoryParkingViewReadModel();
    }

}
