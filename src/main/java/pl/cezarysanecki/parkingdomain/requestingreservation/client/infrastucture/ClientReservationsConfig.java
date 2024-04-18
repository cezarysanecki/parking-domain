package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.CancellingReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.ParkingSpotReservationsEventHandler;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.ReservingPartOfParkingSpot;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.application.ReservingWholeParkingSpot;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationsRepository;

@Configuration
@RequiredArgsConstructor
public class ClientReservationsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    ReservingPartOfParkingSpot reservingPartOfParkingSpot(ClientReservationsRepository clientReservationsRepository) {
        return new ReservingPartOfParkingSpot(clientReservationsRepository);
    }

    @Bean
    ReservingWholeParkingSpot reservingWholeParkingSpot(ClientReservationsRepository clientReservationsRepository) {
        return new ReservingWholeParkingSpot(clientReservationsRepository);
    }

    @Bean
    CancellingReservationRequest cancellingReservationRequest(ClientReservationsRepository clientReservationsRepository) {
        return new CancellingReservationRequest(clientReservationsRepository);
    }

    @Bean
    ParkingSpotReservationsEventHandler parkingSpotReservationsEventHandler(CancellingReservationRequest cancellingReservationRequest) {
        return new ParkingSpotReservationsEventHandler(cancellingReservationRequest);
    }

    @Bean
    @Profile("local")
    ClientReservationsRepository clientReservationsRepository() {
        return new InMemoryClientReservationsRepository(eventPublisher);
    }

}
