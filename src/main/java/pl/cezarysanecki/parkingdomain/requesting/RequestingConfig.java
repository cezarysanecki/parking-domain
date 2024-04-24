package pl.cezarysanecki.parkingdomain.requesting;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.cezarysanecki.parkingdomain.requesting.client.infrastucture.ClientRequestsConfig;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture.ParkingSpotRequestsConfig;
import pl.cezarysanecki.parkingdomain.requesting.view.client.infrastructure.ClientRequestsViewConfig;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.infrastructure.ParkingSpotRequestsViewConfig;

@Configuration
@Import({
        ClientRequestsConfig.class,
        ParkingSpotRequestsConfig.class,
        ClientRequestsViewConfig.class,
        ParkingSpotRequestsViewConfig.class
})
public class RequestingConfig {
}
