package pl.cezarysanecki.parkingdomain.views.client.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.views.client.application.ClientReservationViewEventHandler;
import pl.cezarysanecki.parkingdomain.views.client.model.ClientsReservationsViews;

@Configuration
public class ClientsReservationsViewConfig {

    @Bean
    public ClientReservationViewEventHandler clientReservationViewEventHandler(ClientsReservationsViews clientsReservationsViews) {
        return new ClientReservationViewEventHandler(clientsReservationsViews);
    }

    @Bean
    @Profile("local")
    public ClientsReservationsViews inMemoryClientsReservationsViewRepository() {
        return new InMemoryClientsReservationsViewsRepository();
    }

}
