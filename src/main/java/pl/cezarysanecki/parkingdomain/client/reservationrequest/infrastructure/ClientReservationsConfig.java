package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreatingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFactory;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@Configuration
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final ClientReservationRequestsFactory clientReservationRequestsFactory;

    public ClientReservationsConfig(
            EventPublisher eventPublisher,
            DateProvider dateProvider) {
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
        this.clientReservationRequestsFactory = new ClientReservationRequestsFactory(dateProvider);
    }

    @Bean
    public CreatingReservationRequest reservation(ClientReservationRequestsRepository clientReservationRequestsRepository) {
        return new CreatingReservationRequest(clientReservationRequestsRepository);
    }

    @Bean
    @Profile("local")
    public ClientReservationRequestsRepository inMemoryClientReservationsRepository() {
        DomainModelMapper domainModelMapper = new DomainModelMapper(dateProvider);
        return new InMemoryClientReservationRequestsRepository(
                eventPublisher,
                domainModelMapper,
                clientReservationRequestsFactory);
    }

}
