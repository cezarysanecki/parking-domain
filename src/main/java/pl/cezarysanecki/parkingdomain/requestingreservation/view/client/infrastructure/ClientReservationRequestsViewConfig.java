package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationRequestsViews;

@Configuration
@RequiredArgsConstructor
public class ClientReservationRequestsViewConfig {

    @Bean
    @Profile("local")
    ClientReservationRequestsViews clientReservationRequestsViews() {
        return new InMemoryClientReservationRequestsViewRepository();
    }

}
