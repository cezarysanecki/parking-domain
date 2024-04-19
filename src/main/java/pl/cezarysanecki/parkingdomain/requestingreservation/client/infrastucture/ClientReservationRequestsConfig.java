package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.CancellingReservationRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.RequestingReservationForWholeParkingSpot;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsRepository;

@Configuration
@RequiredArgsConstructor
public class ClientReservationRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    RequestingReservationForPartOfParkingSpot requestingReservationForPartOfParkingSpot(ClientReservationRequestsRepository clientReservationRequestsRepository) {
        return new RequestingReservationForPartOfParkingSpot(clientReservationRequestsRepository);
    }

    @Bean
    RequestingReservationForWholeParkingSpot requestingReservationForWholeParkingSpot(ClientReservationRequestsRepository clientReservationRequestsRepository) {
        return new RequestingReservationForWholeParkingSpot(clientReservationRequestsRepository);
    }

    @Bean
    CancellingReservationRequest cancellingReservationRequest(ClientReservationRequestsRepository clientReservationRequestsRepository) {
        return new CancellingReservationRequest(clientReservationRequestsRepository);
    }

    @Bean
    CancellingReservationRequestEventHandler cancellingReservationRequestEventHandler(CancellingReservationRequest cancellingReservationRequest) {
        return new CancellingReservationRequestEventHandler(cancellingReservationRequest);
    }

    @Bean
    @Profile("local")
    ClientReservationRequestsRepository clientReservationRequestsRepository() {
        return new InMemoryClientReservationRequestsRepository(eventPublisher);
    }

}
