package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.ClientReservationRequestCommandValidator;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.application.CreatingReservationRequest;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;
    private final ClientReservationRequestsRepository clientReservationRequestsRepository;

    @Bean
    public CreatingReservationRequest creatingReservationRequest(ClientReservationRequestCommandValidator clientReservationRequestCommandValidator) {
        return new CreatingReservationRequest(
                clientReservationRequestsRepository,
                clientReservationRequestCommandValidator);
    }

    @Bean
    public CancellingReservationRequest cancellingReservationRequest(ClientReservationRequestCommandValidator clientReservationRequestCommandValidator) {
        return new CancellingReservationRequest(
                clientReservationRequestsRepository,
                clientReservationRequestCommandValidator);
    }

    @Bean
    public ClientReservationRequestCommandValidator clientReservationRequestCommandValidator(DateProvider dateProvider) {
        return new ClientReservationRequestCommandValidator.Production(dateProvider);
    }

    @Bean
    @Profile("local")
    public ClientReservationRequestsRepository inMemoryClientReservationsRepository() {
        return new InMemoryClientReservationRequestsRepository(eventPublisher);
    }

}
