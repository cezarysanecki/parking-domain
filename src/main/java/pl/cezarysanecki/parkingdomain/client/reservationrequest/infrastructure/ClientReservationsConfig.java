package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.ClientReservationRequestValidator;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreatingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@Configuration
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;
    private final ClientReservationRequestValidator clientReservationRequestValidator;
    private final ClientReservationRequestsRepository clientReservationRequestsRepository;

    public ClientReservationsConfig(
            EventPublisher eventPublisher,
            DateProvider dateProvider,
            ClientReservationRequestsRepository clientReservationRequestsRepository) {
        this.eventPublisher = eventPublisher;
        this.clientReservationRequestsRepository = clientReservationRequestsRepository;
        this.clientReservationRequestValidator = new ClientReservationRequestValidator(dateProvider);
    }

    @Bean
    public CreatingReservationRequest creatingReservationRequest() {
        return new CreatingReservationRequest(
                clientReservationRequestsRepository,
                clientReservationRequestValidator);
    }

    @Bean
    public CancellingReservationRequest cancellingReservationRequest() {
        return new CancellingReservationRequest(
                clientReservationRequestsRepository,
                clientReservationRequestValidator);
    }

    @Bean
    @Profile("local")
    public ClientReservationRequestsRepository inMemoryClientReservationsRepository() {
        return new InMemoryClientReservationRequestsRepository(eventPublisher);
    }

}
