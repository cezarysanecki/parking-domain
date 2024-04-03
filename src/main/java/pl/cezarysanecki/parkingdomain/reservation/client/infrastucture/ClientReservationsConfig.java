package pl.cezarysanecki.parkingdomain.reservation.client.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.client.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.reservation.client.application.SubmittingReservationRequestForChosenParkingSpot;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsRepository;

@Configuration
@RequiredArgsConstructor
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    SubmittingReservationRequestForChosenParkingSpot submittingReservationRequestForChosenParkingSpot(ClientReservationsRepository clientReservationsRepository) {
        return new SubmittingReservationRequestForChosenParkingSpot(clientReservationsRepository);
    }

    @Bean
    CancellingReservationRequest cancellingReservationRequest(ClientReservationsRepository clientReservationsRepository) {
        return new CancellingReservationRequest(clientReservationsRepository);
    }

    @Bean
    @Profile("local")
    ClientReservationsRepository clientReservationsRepository() {
        return new InMemoryClientReservationsRepository(eventPublisher);
    }

}
