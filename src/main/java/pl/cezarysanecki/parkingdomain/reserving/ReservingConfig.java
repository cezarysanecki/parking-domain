package pl.cezarysanecki.parkingdomain.reserving;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.cezarysanecki.parkingdomain.reserving.client.infrastucture.ClientReservationsConfig;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.infrastucture.ParkingSpotReservationsConfig;
import pl.cezarysanecki.parkingdomain.reserving.view.client.infrastructure.ClientReservationsViewConfig;
import pl.cezarysanecki.parkingdomain.reserving.view.parkingspot.infrastructure.ParkingSpotReservationsViewConfig;

@Configuration
@Import({
        ClientReservationsConfig.class,
        ParkingSpotReservationsConfig.class,
        ClientReservationsViewConfig.class,
        ParkingSpotReservationsViewConfig.class
})
public class ReservingConfig {
}
