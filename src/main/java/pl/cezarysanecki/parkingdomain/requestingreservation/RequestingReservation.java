package pl.cezarysanecki.parkingdomain.requestingreservation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture.ClientReservationsConfig;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture.ParkingSpotReservationRequestsConfig;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.client.infrastructure.ClientReservationsViewConfig;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure.ParkingSpotReservationsViewConfig;

@Configuration
@Import({
        ClientReservationsConfig.class,
        ParkingSpotReservationRequestsConfig.class,
        ClientReservationsViewConfig.class,
        ParkingSpotReservationsViewConfig.class
})
public class RequestingReservation {
}
