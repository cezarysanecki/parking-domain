package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.ClientReservationsEventHandler;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.RequestingReservation;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsFactory;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsRepository;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@Configuration
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final ClientReservationsFactory clientReservationsFactory;

    public ClientReservationsConfig(
            EventPublisher eventPublisher,
            DateProvider dateProvider) {
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
        this.clientReservationsFactory = new ClientReservationsFactory(dateProvider);
    }

    @Bean
    public RequestingReservation reservation(ClientReservationsRepository clientReservationsRepository) {
        return new RequestingReservation(clientReservationsRepository);
    }

    @Bean
    public ClientReservationsEventHandler clientReservationsEventHandler(ClientReservationsRepository clientReservationsRepository) {
        return new ClientReservationsEventHandler(clientReservationsRepository);
    }

    @Bean
    @Profile("local")
    public ClientReservationsRepository inMemoryClientReservationsRepository() {
        DomainModelMapper domainModelMapper = new DomainModelMapper(dateProvider);
        return new InMemoryClientReservationsRepository(
                eventPublisher,
                domainModelMapper,
                clientReservationsFactory);
    }

}
