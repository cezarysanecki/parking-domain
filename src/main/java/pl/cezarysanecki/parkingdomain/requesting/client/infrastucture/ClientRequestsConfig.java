package pl.cezarysanecki.parkingdomain.requesting.client.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requesting.client.application.CancellingRequest;
import pl.cezarysanecki.parkingdomain.requesting.client.application.CancellingRequestEventHandler;
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.requesting.client.application.MakingRequestForWholeParkingSpot;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsRepository;

@Configuration
@RequiredArgsConstructor
public class ClientRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    MakingRequestForPartOfParkingSpot makingRequestForPartOfParkingSpot(ClientRequestsRepository clientRequestsRepository) {
        return new MakingRequestForPartOfParkingSpot(clientRequestsRepository);
    }

    @Bean
    MakingRequestForWholeParkingSpot makingRequestForWholeParkingSpot(ClientRequestsRepository clientRequestsRepository) {
        return new MakingRequestForWholeParkingSpot(clientRequestsRepository);
    }

    @Bean
    CancellingRequest cancellingRequest(ClientRequestsRepository clientRequestsRepository) {
        return new CancellingRequest(clientRequestsRepository);
    }

    @Bean
    CancellingRequestEventHandler cancellingRequestEventHandler(CancellingRequest cancellingRequest) {
        return new CancellingRequestEventHandler(cancellingRequest);
    }

    @Bean
    @Profile("local")
    InMemoryClientRequestsRepository clientRequestsRepository() {
        return new InMemoryClientRequestsRepository(eventPublisher);
    }

}
