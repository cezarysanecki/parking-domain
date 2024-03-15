package pl.cezarysanecki.parkingdomain.clientreservationsview.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.clientreservationsview.model.ClientsReservationsView;

@Configuration
public class ClientsReservationsViewConfig {

    @Bean
    @Profile("local")
    public ClientsReservationsView inMemoryClientsReservationsViewRepository() {
        return new InMemoryClientsReservationsViewRepository();
    }

}
