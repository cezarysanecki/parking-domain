package pl.cezarysanecki.parkingdomain.reservation.client.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.reservation.client.application.RequestingReservationForChosenParkingSpot;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsRepository;

@Configuration
@RequiredArgsConstructor
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    RequestingReservationForChosenParkingSpot requestingReservationForChosenParkingSpot(ClientReservationsRepository clientReservationsRepository) {
        return new RequestingReservationForChosenParkingSpot(clientReservationsRepository);
    }

    @Bean
    @Profile("local")
    ClientReservationsRepository clientReservationsRepository() {
        return new InMemoryClientReservationsRepository(eventPublisher);
    }

}
