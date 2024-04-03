package pl.cezarysanecki.parkingdomain.reservation.view.parkingspot.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.reservation.view.parkingspot.model.ParkingSpotReservationsViews;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationsViewConfig {

    @Bean
    @Profile("local")
    ParkingSpotReservationsViews parkingSpotReservationsViews() {
        return new InMemoryParkingSpotReservationsViewRepository();
    }

}
