package pl.cezarysanecki.parkingdomain.reservation.parkingspot.infrastucture;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.application.ClientReservationsEventHandler;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationsRepository;

@Configuration
@RequiredArgsConstructor
public class ParkingSpotReservationsConfig {

    @Bean
    ClientReservationsEventHandler clientReservationsEventHandler(ParkingSpotReservationsRepository parkingSpotReservationsRepository) {
        return new ClientReservationsEventHandler(parkingSpotReservationsRepository);
    }

}
