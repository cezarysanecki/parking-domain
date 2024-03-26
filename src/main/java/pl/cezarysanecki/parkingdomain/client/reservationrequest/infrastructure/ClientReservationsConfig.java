package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.ClientReservationRequestValidator;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreatingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFactory;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@Configuration
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;
    private final ClientReservationRequestsFactory clientReservationRequestsFactory;
    private final ClientReservationRequestValidator clientReservationRequestValidator;

    public ClientReservationsConfig(
            EventPublisher eventPublisher,
            DateProvider dateProvider) {
        this.eventPublisher = eventPublisher;
        this.clientReservationRequestsFactory = new ClientReservationRequestsFactory(dateProvider);
        this.clientReservationRequestValidator = new ClientReservationRequestValidator(dateProvider);
    }

    @Bean
    public CreatingReservationRequest creatingReservationRequest(ClientReservationRequestsRepository clientReservationRequestsRepository) {
        return new CreatingReservationRequest(
                clientReservationRequestsRepository,
                clientReservationRequestValidator,
                clientReservationRequestsFactory);
    }

    @Bean
    public CancellingReservationRequest cancellingReservationRequest(ClientReservationRequestsRepository clientReservationRequestsRepository) {
        return new CancellingReservationRequest(
                clientReservationRequestsRepository,
                clientReservationRequestValidator);
    }

    @Bean
    @Profile("local")
    public ClientReservationRequestsRepository inMemoryClientReservationsRepository() {
        DomainModelMapper domainModelMapper = new DomainModelMapper(clientReservationRequestsFactory);
        return new InMemoryClientReservationRequestsRepository(
                eventPublisher,
                domainModelMapper);
    }

}
