package pl.cezarysanecki.parkingdomain.reservation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.cezarysanecki.parkingdomain.reservation.client.infrastucture.ClientReservationsConfig;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.infrastucture.ParkingSpotReservationsConfig;
import pl.cezarysanecki.parkingdomain.reservation.view.client.infrastructure.ClientReservationsViewConfig;
import pl.cezarysanecki.parkingdomain.reservation.view.parkingspot.infrastructure.ParkingSpotReservationsViewConfig;

@Configuration
@Import({
        ClientReservationsConfig.class,
        ParkingSpotReservationsConfig.class,
        ClientReservationsViewConfig.class,
        ParkingSpotReservationsViewConfig.class
})
public class ReservationConfig {
}
