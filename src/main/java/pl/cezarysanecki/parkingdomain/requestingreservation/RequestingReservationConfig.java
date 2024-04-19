package pl.cezarysanecki.parkingdomain.requestingreservation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture.ClientReservationRequestsConfig;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture.ParkingSpotReservationRequestsConfig;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure.ClientReservationRequestsViewConfig;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure.ParkingSpotReservationsViewConfig;

@Configuration
@Import({
        ClientReservationRequestsConfig.class,
        ParkingSpotReservationRequestsConfig.class,
        ClientReservationRequestsViewConfig.class,
        ParkingSpotReservationsViewConfig.class
})
public class RequestingReservationConfig {
}
