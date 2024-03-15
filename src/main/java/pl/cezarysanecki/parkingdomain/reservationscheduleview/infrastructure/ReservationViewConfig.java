package pl.cezarysanecki.parkingdomain.reservationscheduleview.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.reservationscheduleview.model.ReservationsViews;

@Configuration
public class ReservationViewConfig {

    @Bean
    @Profile("local")
    public ReservationsViews inMemoryReservationsViewsRepository() {
        return new InMemoryReservationsViewReadModel();
    }

}
