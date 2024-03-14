package pl.cezarysanecki.parkingdomain.reservationview.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.reservationview.model.ReservationsViews;

@Configuration
public class ReservationViewConfig {

    @Bean
    @Profile("local")
    public ReservationsViews inMemoryReservationsViewsRepository() {
        return new InMemoryReservationsViewReadModel();
    }

}
