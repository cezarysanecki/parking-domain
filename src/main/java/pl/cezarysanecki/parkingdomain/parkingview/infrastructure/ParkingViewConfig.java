package pl.cezarysanecki.parkingdomain.parkingview.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.parkingview.application.ParkingSpotViewEventHandler;
import pl.cezarysanecki.parkingdomain.parkingview.model.ParkingViews;

@Configuration
public class ParkingViewConfig {

    @Bean
    public ParkingSpotViewEventHandler parkingSpotViewEventHandler(ParkingViews parkingViews) {
        return new ParkingSpotViewEventHandler(parkingViews);
    }

    @Bean
    @Profile("local")
    public ParkingViews inMemoryParkingViewsRepository() {
        return new InMemoryParkingViewReadModel();
    }

}
