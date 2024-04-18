package pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.model.ClientReservationsViews;

@Configuration
@RequiredArgsConstructor
public class ClientReservationsViewConfig {

    @Bean
    @Profile("local")
    ClientReservationsViews clientReservationsViews() {
        return new InMemoryClientReservationsViewRepository();
    }

}
